package com.expiredminotaur.bcukbot.web.view.collection;

import com.expiredminotaur.bcukbot.sql.collection.clip.Clip;
import com.expiredminotaur.bcukbot.sql.collection.clip.ClipRepository;
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

@Route(value = "clips", layout = MainLayout.class)
public class ClipView extends VerticalLayout
{
    private final Grid<Clip> clipGrid = new Grid<>(Clip.class);
    @Autowired
    private ClipRepository clips;

    public ClipView(@Autowired UserTools userTools)
    {
        setSizeFull();
        H2 header = new H2("Clips");
        clipGrid.setColumns("id", "clip", "source", "date");
        clipGrid.setSizeFull();

        if (userTools.isCurrentUserAdmin())
        {
            clipGrid.addColumn(new ComponentRenderer<>(clip -> new Button("Edit", e -> edit(clip))))
                    .setHeader("Edit")
                    .setFlexGrow(0);
        }

        clipGrid.getColumns().forEach(c -> c.setAutoWidth(true));
        clipGrid.recalculateColumnWidths();
        add(header, clipGrid);
    }

    @PostConstruct
    private void initData()
    {
        clipGrid.setItems(clips.findAll());
    }

    private void edit(Clip clip)
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

        Binder<Clip> binder = new Binder<>(Clip.class);
        binder.forField(quoteField).bind("clip");
        layout.addFormItem(quoteField, "Clip");

        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e ->
        {
            try
            {
                binder.writeBean(clip);
                clips.save(clip);
                clipGrid.getDataProvider().refreshItem(clip);
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

        binder.readBean(clip);
        editDialog.open();
    }
}
