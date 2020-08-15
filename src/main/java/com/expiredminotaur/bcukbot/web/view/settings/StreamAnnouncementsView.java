package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.sql.twitch.streams.group.Group;
import com.expiredminotaur.bcukbot.sql.twitch.streams.group.GroupRepository;
import com.expiredminotaur.bcukbot.sql.twitch.streams.streamer.Streamer;
import com.expiredminotaur.bcukbot.sql.twitch.streams.streamer.StreamerRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "settings/stream_announcements", layout = MainLayout.class)
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
        Button addGroup = new Button("Add Group", e -> addGroup());
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

    private void addGroup()
    {
        Dialog addGroupDialog = new Dialog();
        addGroupDialog.setCloseOnOutsideClick(false);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        TextField nameFiled = new TextField();
        nameFiled.setWidthFull();

        Binder<Group> binder = new Binder<>(Group.class);
        binder.forField(nameFiled).bind("name");
        layout.addFormItem(nameFiled, "Name");

        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e ->
        {
            try
            {
                Group newGroup = new Group();
                binder.writeBean(newGroup);
                groups.save(newGroup);
                initData();
                addGroupDialog.close();

            } catch (ValidationException ex)
            {
                ex.printStackTrace();
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> addGroupDialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(save, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        addGroupDialog.add(layout, buttons);

        addGroupDialog.open();
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

            streamersGrid.setColumns("name");
            Button addStreamer = new Button("Add Streamer", e -> addStreamer());
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

        private void addStreamer()
        {
            Dialog addStreamerDialog = new Dialog();
            addStreamerDialog.setCloseOnOutsideClick(false);
            FormLayout layout = new FormLayout();
            layout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

            TextField nameFiled = new TextField();
            nameFiled.setWidthFull();

            Binder<Streamer> binder = new Binder<>(Streamer.class);
            binder.forField(nameFiled).bind("name");
            layout.addFormItem(nameFiled, "Name");

            HorizontalLayout buttons = new HorizontalLayout();
            Button save = new Button("Save", e ->
            {
                try
                {
                    Streamer newStreamer = new Streamer();
                    binder.writeBean(newStreamer);
                    newStreamer.setGroup(group);
                    streamers.save(newStreamer);
                    group.getStreamers().add(newStreamer);
                    streamersGrid.getDataProvider().refreshAll();
                    addStreamerDialog.close();

                } catch (ValidationException ex)
                {
                    ex.printStackTrace();
                }
            });
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button cancel = new Button("Cancel", e -> addStreamerDialog.close());
            cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            buttons.add(save, cancel);
            buttons.setJustifyContentMode(JustifyContentMode.END);

            addStreamerDialog.add(layout, buttons);

            addStreamerDialog.open();
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
    }
}
