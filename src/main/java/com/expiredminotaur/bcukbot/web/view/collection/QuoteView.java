package com.expiredminotaur.bcukbot.web.view.collection;

import com.expiredminotaur.bcukbot.sql.collection.quote.Quote;
import com.expiredminotaur.bcukbot.sql.collection.quote.QuoteRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.UserTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
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

@Route(value = "quotes", layout = MainLayout.class)
public class QuoteView extends VerticalLayout
{
    private final Grid<Quote> quoteGrid = new Grid<>(Quote.class);
    @Autowired
    private QuoteRepository quotes;

    public QuoteView(@Autowired UserTools userTools)
    {
        setSizeFull();
        H2 header = new H2("Quotes");
        quoteGrid.setColumns("id", "quote", "source", "date");
        quoteGrid.setSizeFull();

        if (userTools.isCurrentUserAdmin())
        {
            quoteGrid.addColumn(new ComponentRenderer<>(quote -> new Button("Edit", e -> edit(quote))))
                    .setHeader("Edit")
                    .setFlexGrow(0);
        }

        quoteGrid.getColumns().forEach(c -> c.setAutoWidth(true));
        quoteGrid.recalculateColumnWidths();
        add(header, quoteGrid);
    }

    @PostConstruct
    private void initData()
    {
        quoteGrid.setItems(quotes.findAll());
    }

    private void edit(Quote quote)
    {
        Dialog editDialog = new Dialog();
        editDialog.setWidth("60%");
        editDialog.setCloseOnOutsideClick(false);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new ResponsiveStep("0", 1, LabelsPosition.TOP),
                new ResponsiveStep("600px", 1, LabelsPosition.ASIDE));

        TextField quoteField = new TextField();
        quoteField.setWidthFull();

        Binder<Quote> binder = new Binder<>(Quote.class);
        binder.forField(quoteField).bind("quote");
        layout.addFormItem(quoteField, "Quote");

        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e ->
        {
            try
            {
                binder.writeBean(quote);
                quotes.save(quote);
                quoteGrid.getDataProvider().refreshItem(quote);
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

        binder.readBean(quote);
        editDialog.open();
    }
}
