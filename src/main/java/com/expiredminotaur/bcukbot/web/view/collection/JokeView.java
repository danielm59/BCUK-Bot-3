package com.expiredminotaur.bcukbot.web.view.collection;

import com.expiredminotaur.bcukbot.sql.collection.joke.Joke;
import com.expiredminotaur.bcukbot.sql.collection.joke.JokeRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.UserTools;
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "jokes", layout = MainLayout.class)
public class JokeView extends VerticalLayout
{
    private final Grid<Joke> jokeGrid = new Grid<>(Joke.class);
    @Autowired
    private JokeRepository jokes;

    public JokeView(@Autowired UserTools userTools)
    {
        setSizeFull();
        H2 header = new H2("Jokes");
        jokeGrid.setColumns("id", "joke", "source", "date");
        jokeGrid.setSizeFull();

        if (userTools.isCurrentUserAdmin())
        {
            jokeGrid.addColumn(new ComponentRenderer<>(joke -> new Button("Edit", e -> edit(joke))))
                    .setHeader("Edit")
                    .setFlexGrow(0);
        }

        jokeGrid.getColumns().forEach(c -> c.setAutoWidth(true));
        jokeGrid.recalculateColumnWidths();
        add(header, jokeGrid);
    }

    @PostConstruct
    private void initData()
    {
        jokeGrid.setItems(jokes.findAll());
    }

    private void edit(Joke joke)
    {
        Dialog editDialog = new Dialog();
        editDialog.setWidth("60%");
        editDialog.setCloseOnOutsideClick(false);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        TextField quoteField = new TextField();
        quoteField.setWidthFull();

        Binder<Joke> binder = new Binder<>(Joke.class);
        binder.forField(quoteField).bind("quote");
        layout.addFormItem(quoteField, "Quote");

        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e ->
        {
            try
            {
                binder.writeBean(joke);
                jokes.save(joke);
                jokeGrid.getDataProvider().refreshItem(joke);
                editDialog.close();

            } catch (ValidationException ex)
            {
                ex.printStackTrace();
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> editDialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(save, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        editDialog.add(layout, buttons);

        binder.readBean(joke);
        editDialog.open();
    }
}
