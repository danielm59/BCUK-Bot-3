package com.expiredminotaur.bcukbot.web.view.admin;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
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
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "users", layout = MainLayout.class)
@AccessLevel(Role.ADMIN)
public class UsersView extends VerticalLayout
{
    private final UserRepository users;
    private final Grid<User> grid = new Grid<>(User.class);
    private final EditForm editForm = new EditForm();

    @Autowired
    public UsersView(UserRepository users)
    {
        this.users = users;
        setSizeFull();
        grid.setColumns("discordId", "discordName", "twitchName", "accessLevel", "twitchBotEnabled");
        grid.addColumn(new ComponentRenderer<>(user -> new Button("Edit", e -> editForm.open(user))))
                .setHeader("Edit")
                .setFlexGrow(0);
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.setItems(users.findAll());
        add(grid);
    }

    private class EditForm
    {
        private final FormLayout layout = new FormLayout();
        private final Binder<User> binder = new Binder<>(User.class);

        EditForm()
        {
            TextField discordId = new TextField();
            TextField discordName = new TextField();
            TextField twitchName = new TextField();
            ComboBox<Role> accessLevel = new ComboBox<>();
            Checkbox twitchBotEnabled = new Checkbox();

            discordId.setEnabled(false);
            discordName.setEnabled(false);
            accessLevel.setItems(Role.values());

            layout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

            layout.addFormItem(discordId, "Discord ID");
            layout.addFormItem(discordName, "Discord Name");
            layout.addFormItem(twitchName, "Twitch Name");
            layout.addFormItem(accessLevel, "Access Level");
            layout.addFormItem(twitchBotEnabled, "Twitch Bot Enabled");

            binder.forField(discordId).withConverter(new StringToLongConverter("")).bind("discordId");
            binder.forField(discordName).bind("discordName");
            binder.forField(twitchName).bind("twitchName");
            binder.forField(accessLevel).bind("accessLevel");
            binder.forField(twitchBotEnabled).bind("twitchBotEnabled");
        }

        void open(User user)
        {
            binder.readBean(user);
            Dialog dialog = new Dialog();
            dialog.setWidth("60%");
            dialog.setCloseOnOutsideClick(false);
            dialog.add(layout, createButtons(user, dialog));
            dialog.open();
        }

        private HorizontalLayout createButtons(User data, Dialog dialog)
        {
            HorizontalLayout buttons = new HorizontalLayout();
            Button save = new Button("Save", e -> save(data, dialog));
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button cancel = new Button("Cancel", e -> dialog.close());
            cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            buttons.add(save, cancel);
            buttons.setJustifyContentMode(JustifyContentMode.END);
            return buttons;
        }

        private void save(User data, Dialog dialog)
        {
            try
            {
                binder.writeBean(data);
                users.save(data);
                grid.getDataProvider().refreshItem(data);
                dialog.close();

            } catch (ValidationException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
