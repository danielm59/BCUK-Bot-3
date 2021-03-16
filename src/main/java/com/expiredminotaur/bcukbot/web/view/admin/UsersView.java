package com.expiredminotaur.bcukbot.web.view.admin;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.web.component.Form;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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

    private class EditForm extends Form<User>
    {
        EditForm()
        {
            super(User.class);

            TextField discordId = new TextField();
            addField("Discord ID", discordId, "discordId", new StringToLongConverter(""));
            discordId.setEnabled(false);

            TextField discordName = new TextField();
            addField("Discord Name", discordName, "discordName", null);
            discordName.setEnabled(false);

            addField("Twitch Name", new TextField(), "twitchName", null);

            ComboBox<Role> accessLevel = new ComboBox<>();
            addField("Access Level", accessLevel, "accessLevel", null);
            accessLevel.setItems(Role.values());

            addField("Twitch Bot Enabled", new Checkbox(), "twitchBotEnabled", null);
        }

        @Override
        protected void saveData(User data)
        {
            users.save(data);
            grid.getDataProvider().refreshItem(data);
        }
    }
}
