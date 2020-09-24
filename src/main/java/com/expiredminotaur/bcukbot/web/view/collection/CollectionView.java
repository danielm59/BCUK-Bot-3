package com.expiredminotaur.bcukbot.web.view.collection;

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
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public abstract class CollectionView<T> extends VerticalLayout
{
    private final Grid<T> grid;
    private final Binder<T> binder;
    private final UserTools userTools;
    private final CrudRepository<T, Integer> repository;
    private String dataField;
    private String label;

    public CollectionView(UserTools userTools, CrudRepository<T, Integer> repository, Grid<T> grid, Binder<T> binder)
    {
        this.userTools = userTools;
        this.repository = repository;
        this.grid = grid;
        this.binder = binder;
    }

    protected void setup(String title, String dataField, String label)
    {
        this.dataField = dataField;
        this.label = label;
        setSizeFull();
        H2 header = new H2(title);
        grid.setColumns("id", dataField, "source", "date");
        grid.setSizeFull();

        if (userTools.isCurrentUserAdmin())
        {
            grid.addColumn(new ComponentRenderer<>(joke -> new Button("Edit", e -> edit(joke))))
                    .setHeader("Edit")
                    .setFlexGrow(0);
        }

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.recalculateColumnWidths();

        grid.setItems((ArrayList<T>) repository.findAll());

        add(header, grid);
    }

    private void edit(T data)
    {
        Dialog editDialog = new Dialog();
        editDialog.setWidth("60%");
        editDialog.setCloseOnOutsideClick(false);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        TextField textField = new TextField();
        textField.setWidthFull();

        binder.forField(textField).bind(dataField);
        layout.addFormItem(textField, label);

        editDialog.add(layout, createButtons(data, editDialog));

        binder.readBean(data);
        editDialog.open();
    }

    private HorizontalLayout createButtons(T data, Dialog editDialog)
    {
        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e -> save(data, editDialog));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> editDialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(save, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.END);
        return buttons;
    }

    private void save(T data, Dialog editDialog)
    {
        try
        {
            binder.writeBean(data);
            repository.save(data);
            grid.getDataProvider().refreshItem(data);
            editDialog.close();

        } catch (ValidationException ex)
        {
            ex.printStackTrace();
        }
    }
}
