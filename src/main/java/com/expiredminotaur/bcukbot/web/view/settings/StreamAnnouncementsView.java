package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.twitch.streams.group.Group;
import com.expiredminotaur.bcukbot.sql.twitch.streams.group.GroupRepository;
import com.expiredminotaur.bcukbot.sql.twitch.streams.streamer.Streamer;
import com.expiredminotaur.bcukbot.sql.twitch.streams.streamer.StreamerRepository;
import com.expiredminotaur.bcukbot.web.component.Form;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "stream_announcements", layout = MainLayout.class)
@AccessLevel(Role.MANAGER)
public class StreamAnnouncementsView extends VerticalLayout
{
    private final ComboBox<Group> groupList = new ComboBox<>();
    private final GroupInfo groupInfo = new GroupInfo();
    @Autowired
    private GroupRepository groups;
    @Autowired
    private StreamerRepository streamers;

    public StreamAnnouncementsView()
    {
        setSizeFull();
        HorizontalLayout groupLayout = new HorizontalLayout();
        groupList.setItemLabelGenerator(Group::getName);
        groupList.setClearButtonVisible(true);
        groupList.addValueChangeListener(this::groupChangeEvent);
        AddGroupForm addGroupForm = new AddGroupForm();
        Button addGroup = new Button("Add Group", e -> addGroupForm.open(new Group()));
        groupLayout.add(groupList, addGroup);
        groupInfo.setVisible(false);
        add(groupLayout, groupInfo);
    }

    @PostConstruct
    private void initData()
    {
        groupList.setItems(groups.findAll());
    }

    private void groupChangeEvent(ComponentValueChangeEvent<ComboBox<Group>, Group> event)
    {
        if (event.getSource().isEmpty())
        {
            groupInfo.setVisible(false);
        } else
        {
            groupInfo.setGroup(event.getValue());
            groupInfo.setVisible(true);
        }
    }

    private class GroupInfo extends HorizontalLayout
    {
        private final Binder<Group> binder = new Binder<>(Group.class);
        private final Grid<Streamer> streamersGrid = new Grid<>(Streamer.class);
        private Group group;

        public GroupInfo()
        {
            setWidthFull();
            VerticalLayout infoLayout = new VerticalLayout();
            VerticalLayout StreamerLayout = new VerticalLayout();

            TextField discordChannelId = new TextField("Discord Channel ID");
            Checkbox deleteOld = new Checkbox("Delete Old Messages?");
            Checkbox multiTwitch = new Checkbox("Post MultiTwitch Links");
            TextField liveMessage = new TextField("Gone Live Message");
            liveMessage.setWidth("100%");
            TextField gameMessage = new TextField("Change Game Message");
            gameMessage.setWidth("100%");
            TextField multiTwitchMessage = new TextField("MultiTwitch Message");
            multiTwitchMessage.setWidth("100%");
            Button save = new Button("Save", e -> saveGroup());
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            EditStreamerForm streamerForm = new EditStreamerForm();

            streamersGrid.setColumns("name");
            streamersGrid.addColumn(new ComponentRenderer<>(streamer -> new Button("Edit", e -> streamerForm.open(streamer))))
                    .setHeader("Edit")
                    .setFlexGrow(0);
            streamersGrid.addColumn(new ComponentRenderer<>(streamer -> new Button("Delete", e -> deleteStreamer(streamer))))
                    .setHeader("Delete")
                    .setFlexGrow(0);
            streamersGrid.getColumns().forEach(c -> c.setAutoWidth(true));
            streamersGrid.recalculateColumnWidths();

            Button addStreamer = new Button("Add Streamer", e -> streamerForm.open(new Streamer()));
            addStreamer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            binder.forField(discordChannelId)
                    .withConverter(Long::valueOf, String::valueOf, "Please enter a number")
                    .bind("discordChannel");
            binder.forField(deleteOld).bind("deleteOldPosts");
            binder.forField(multiTwitch).bind("multiTwitch");
            binder.forField(liveMessage).bind("liveMessage");
            binder.forField(gameMessage).bind("newGameMessage");
            binder.forField(multiTwitchMessage).bind("multiTwitchMessage");

            infoLayout.add(discordChannelId, deleteOld, multiTwitch, liveMessage, gameMessage, multiTwitchMessage, save);
            StreamerLayout.add(streamersGrid, addStreamer);

            add(infoLayout, StreamerLayout);
        }

        private void deleteStreamer(Streamer streamer)
        {
            Dialog deleteStreamerDialog = new Dialog();
            deleteStreamerDialog.setCloseOnOutsideClick(false);
            Text message = new Text("Are you sure you want to delete " + streamer.getName() +
                    " from the group " + streamer.getGroup().getName() + "?");
            HorizontalLayout buttons = new HorizontalLayout();
            Button yes = new Button("Yes", e ->
            {
                streamers.delete(streamer);
                group.getStreamers().remove(streamer);
                streamersGrid.getDataProvider().refreshAll();
                deleteStreamerDialog.close();
            });
            yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button no = new Button("No", e -> deleteStreamerDialog.close());
            no.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            buttons.add(yes, no);
            buttons.setJustifyContentMode(JustifyContentMode.END);

            deleteStreamerDialog.add(message, buttons);
            deleteStreamerDialog.open();
        }

        private void saveGroup()
        {
            try
            {
                if (binder.validate().isOk())
                {
                    binder.writeBean(group);
                    groups.save(group);
                }
            } catch (ValidationException ignore)
            {
            }
        }

        public void setGroup(Group group)
        {
            this.group = group;
            binder.readBean(group);
            streamersGrid.setItems(group.getStreamers());
        }

        private class EditStreamerForm extends Form<Streamer>
        {

            public EditStreamerForm()
            {
                super(Streamer.class);
                addField("Name", new TextField(), "name").setWidthFull();
            }

            @Override
            protected void saveData(Streamer data)
            {
                streamers.save(data);
                group.getStreamers().add(data);
                streamersGrid.getDataProvider().refreshAll();
            }
        }
    }

    private class AddGroupForm extends Form<Group>
    {

        public AddGroupForm()
        {
            super(Group.class);
            addField("Name", new TextField(), "name").setWidthFull();
        }

        @Override
        protected void saveData(Group data)
        {
            groups.save(data);
            initData();
        }
    }
}