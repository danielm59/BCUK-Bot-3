package com.expiredminotaur.bcukbot.web.view.commands;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.counter.Counter;
import com.expiredminotaur.bcukbot.sql.counter.CounterRepository;
import com.expiredminotaur.bcukbot.web.component.Form;
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
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "counters", layout = MainLayout.class)
@AccessLevel(Role.MOD)
public class CountersView extends VerticalLayout
{
    private final Grid<Counter> counterGrid = new Grid<>(Counter.class);
    @Autowired
    private CounterRepository counters;
    private final CounterForm counterForm = new CounterForm();

    public CountersView()
    {
        setSizeFull();
        H2 header = new H2("Counters");
        counterGrid.setColumns("triggerCommand", "checkCommand", "incrementMessage", "message", "currentValue");
        counterGrid.getColumns().forEach(c -> c.setAutoWidth(true));
        HorizontalLayout buttons = new HorizontalLayout();
        Button addBlameButton = new Button("Add Blame", e -> addBlame());
        addBlameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button addCounterButton = new Button("Add Counter", e -> counterForm.open(new Counter()));
        addCounterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttons.add(addBlameButton, addCounterButton);
        add(header, counterGrid, buttons);
    }

    @PostConstruct
    private void initData()
    {
        counterGrid.setItems(counters.findAll());
        counterGrid.recalculateColumnWidths();
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

            counterForm.open(blameCounter);

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

    private class CounterForm extends Form<Counter>
    {

        public CounterForm()
        {
            super(Counter.class);
            addField("Trigger Command", new TextField(), "triggerCommand").setWidthFull();
            addField("Check Command", new TextField(), "checkCommand").setWidthFull();
            addField("Increment Message", new TextField(), "incrementMessage").setWidthFull();
            addField("Message", new TextField(), "message").setWidthFull();
        }

        @Override
        protected void saveData(Counter data)
        {
            counters.save(data);
            initData();
        }
    }
}
