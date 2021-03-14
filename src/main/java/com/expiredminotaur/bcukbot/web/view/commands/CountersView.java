package com.expiredminotaur.bcukbot.web.view.commands;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.counter.Counter;
import com.expiredminotaur.bcukbot.sql.counter.CounterRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "settings/counters", layout = MainLayout.class)
@AccessLevel(Role.MOD)
public class CountersView extends VerticalLayout
{
    private final Grid<Counter> counterGrid = new Grid<>(Counter.class);
    @Autowired
    private CounterRepository counters;

    public CountersView()
    {
        setSizeFull();
        H2 header = new H2("Counters");
        counterGrid.setColumns("triggerCommand", "checkCommand", "incrementMessage", "message", "currentValue");
        counterGrid.getColumns().forEach(c -> c.setAutoWidth(true));
        HorizontalLayout buttons = new HorizontalLayout();
        Button addBlameButton = new Button("Add Blame", e -> addBlame());
        addBlameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button addCounterButton = new Button("Add Counter", e -> addCounter(new Counter()));
        addCounterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttons.add(addBlameButton, addCounterButton);
        add(header, counterGrid, buttons);
    }

    @PostConstruct
    private void initData()
    {
        counterGrid.setItems(counters.findAll());
    }

    private void addBlame()
    {
        Dialog blameDialog = new Dialog();
        FormLayout layout = new FormLayout();

        TextField nameField = new TextField();
        layout.addFormItem(nameField, "Name");
        nameField.setWidthFull();

        HorizontalLayout buttons = new HorizontalLayout();
        Button ok = new Button("OK", e ->
        {
            String name = nameField.getValue();
            Counter blameCounter = new Counter();
            blameCounter.setTriggerCommand(String.format("#Blame%s", name));
            blameCounter.setCheckCommand(String.format("!Blame%sCheck", name));
            blameCounter.setIncrementMessage(String.format("%s has been blamed again!", name));
            blameCounter.setMessage(String.format("%s has been blamed %%d times this year", name));

            addCounter(blameCounter);

            blameDialog.close();
        });
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> blameDialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(ok, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        blameDialog.add(layout, buttons);
        blameDialog.open();
    }

    private void addCounter(Counter counter)
    {
        Binder<Counter> binder = new Binder<>(Counter.class);

        Dialog addCounterDialog = new Dialog();
        addCounterDialog.setCloseOnOutsideClick(false);
        FormLayout layout = new FormLayout();

        TextField triggerCommand = new TextField();
        layout.addFormItem(triggerCommand, "Trigger Command");
        binder.bind(triggerCommand, "triggerCommand");
        triggerCommand.setWidthFull();

        TextField checkCommand = new TextField();
        layout.addFormItem(checkCommand, "Check Command");
        binder.bind(checkCommand, "checkCommand");
        checkCommand.setWidthFull();

        TextField incrementMessage = new TextField();
        layout.addFormItem(incrementMessage, "Increment Message");
        binder.bind(incrementMessage, "incrementMessage");
        incrementMessage.setWidthFull();

        TextField message = new TextField();
        layout.addFormItem(message, "Message");
        binder.bind(message, "message");
        message.setWidthFull();

        binder.readBean(counter);

        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e ->
        {
            try
            {
                binder.writeBean(counter);
                counters.save(counter);
                initData();
                addCounterDialog.close();

            } catch (ValidationException ex)
            {
                ex.printStackTrace();
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> addCounterDialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(save, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        addCounterDialog.add(layout, buttons);
        addCounterDialog.open();
    }
}
