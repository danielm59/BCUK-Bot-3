package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.sql.command.custom.CommandRepository;
import com.expiredminotaur.bcukbot.sql.command.custom.CustomCommand;
import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
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
    private final UserRepository users;
    private final CommandRepository commands;
    private final Grid<CustomCommand> grid;


    public CommandsView(@Autowired UserRepository users, @Autowired CommandRepository commands)
    {
        this.users = users;
        this.commands = commands;

        setSizeFull();

        TextField trigger = new TextField("Trigger");
        TextField output = new TextField("Output");
        output.setWidthFull();
        Checkbox discord = new Checkbox("Enable on Discord");
        MultiselectComboBox<User> twitchUsers = new MultiselectComboBox<>("Users");
        twitchUsers.setItemLabelGenerator(User::getTwitchName);
        twitchUsers.setItems(users.chatBotUsers());

        Binder<CustomCommand> binder = new Binder<>(CustomCommand.class);
        binder.bind(trigger, "trigger");
        binder.bind(output, "output");
        binder.bind(discord, "discordEnabled");
        binder.bind(twitchUsers, "twitchEnabledUsers");

        grid = new Grid<>(CustomCommand.class);
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
        Dialog editDialog = new Dialog();
        editDialog.setWidth("60%");
        editDialog.setCloseOnOutsideClick(false);

        Binder<CustomCommand> binder = new Binder<>(CustomCommand.class);
        editDialog.add(createForm(binder), createButtons(binder, command, editDialog));

        binder.readBean(command);
        editDialog.open();
    }

    private FormLayout createForm(Binder<CustomCommand> binder)
    {
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        TextField trigger = new TextField();
        TextField output = new TextField();
        output.setWidthFull();
        Checkbox discord = new Checkbox();
        MultiselectComboBox<User> twitchUsers = new MultiselectComboBox<>();
        twitchUsers.setItemLabelGenerator(User::getTwitchName);
        twitchUsers.setItems(users.chatBotUsers());

        binder.bind(trigger, "trigger");
        binder.bind(output, "output");
        binder.bind(discord, "discordEnabled");
        binder.bind(twitchUsers, "twitchEnabledUsers");

        layout.addFormItem(trigger, "Trigger");
        layout.addFormItem(output, "Output");
        layout.addFormItem(discord, "Enable on Discord");
        layout.addFormItem(twitchUsers, "Users");
        return layout;
    }

    private HorizontalLayout createButtons(Binder<CustomCommand> binder, CustomCommand command, Dialog editDialog)
    {
        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e ->
        {
            try
            {
                binder.writeBean(command);
                commands.save(command);
                binder.readBean(new CustomCommand());
                grid.setItems(commands.findAll());
                grid.recalculateColumnWidths();
            } catch (ValidationException validationException)
            {
                validationException.printStackTrace();
            }
        }
        );
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> editDialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(save, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.END);
        return buttons;
    }

}
