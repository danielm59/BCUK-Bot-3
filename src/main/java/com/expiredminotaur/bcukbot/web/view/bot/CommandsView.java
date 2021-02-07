package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.sql.command.custom.CommandRepository;
import com.expiredminotaur.bcukbot.sql.command.custom.CustomCommand;
import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.stream.Collectors;

@Route(value = "commands", layout = MainLayout.class)
public class CommandsView extends HorizontalLayout
{
    public CommandsView(@Autowired UserRepository users, @Autowired CommandRepository commands)
    {
        setSizeFull();

        TextField trigger = new TextField("Trigger");
        TextField output = new TextField("Output");
        Checkbox discord = new Checkbox("Enable on Discord");
        MultiselectComboBox<User> twitchUsers = new MultiselectComboBox<>("Users");
        twitchUsers.setItemLabelGenerator(User::getTwitchName);
        twitchUsers.setItems(users.chatBotUsers());

        Binder<CustomCommand> binder = new Binder<>(CustomCommand.class);
        binder.bind(trigger, "trigger");
        binder.bind(output, "output");
        binder.bind(discord, "discordEnabled");
        binder.bind(twitchUsers, "twitchEnabledUsers");

        Grid<CustomCommand> grid = new Grid<>(CustomCommand.class);
        grid.setItems(commands.findAll());
        grid.setColumns("trigger", "output", "discordEnabled");
        grid.addColumn(c -> c.getTwitchEnabledUsers().stream().map(User::getTwitchName).collect(Collectors.joining(", "))).setHeader("Twitch Enabled");
        grid.addColumn(new ComponentRenderer<>(c -> new Button("Edit", e -> edit(c)))).setFlexGrow(0).setHeader("Edit");
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.recalculateColumnWidths();

        Button add = new Button("Add", e ->
        {
            try
            {
                CustomCommand command = new CustomCommand();
                binder.writeBean(command);
                commands.save(command);
                binder.readBean(new CustomCommand());
                grid.setItems(commands.findAll());
                grid.recalculateColumnWidths();
            } catch (ValidationException validationException)
            {
                validationException.printStackTrace();
            }
        });

        VerticalLayout left = new VerticalLayout(trigger, trigger, output, discord, twitchUsers, add);
        left.setSizeFull();
        left.setWidth("25%");
        VerticalLayout right = new VerticalLayout(grid);
        right.setSizeFull();
        add(left, right);
        setFlexGrow(1, right);
    }

    private void edit(CustomCommand command)
    {
        //TODO edit dialog
    }
}
