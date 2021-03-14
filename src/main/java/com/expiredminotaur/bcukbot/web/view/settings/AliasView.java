package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.command.alias.Alias;
import com.expiredminotaur.bcukbot.sql.command.alias.AliasRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "settings/alias", layout = MainLayout.class)
@AccessLevel(Role.MOD)
public class AliasView extends VerticalLayout
{
    private final AliasRepository aliasRepository;
    private final Grid<Alias> grid = new Grid<>(Alias.class);
    private final Binder<Alias> binder = new Binder<>(Alias.class);

    public AliasView(@Autowired AliasRepository aliasRepository)
    {
        this.aliasRepository = aliasRepository;
        setSizeFull();
        Button addButton = new Button("Add", e -> edit(new Alias()));
        grid.setColumns("shortCommand", "fullCommand");
        grid.setItems(aliasRepository.findAll());
        grid.addColumn(new ComponentRenderer<>(alias -> new Button("Edit", e -> edit(alias))))
                .setHeader("Edit")
                .setFlexGrow(0);
        grid.addColumn(new ComponentRenderer<>(alias -> new Button("Delete", e -> delete(alias))))
                .setHeader("Delete")
                .setFlexGrow(0);
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        add(addButton, grid);
    }

    private void edit(Alias alias)
    {
        Dialog dialog = new Dialog();
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        TextField shortC = new TextField();
        shortC.setWidthFull();
        shortC.setRequired(true);
        layout.addFormItem(shortC, "Short Command");
        binder.bind(shortC, "shortCommand");

        TextField fullC = new TextField();
        fullC.setWidthFull();
        fullC.setRequired(true);
        layout.addFormItem(fullC, "Full Command");
        binder.bind(fullC, "fullCommand");

        dialog.add(layout, createEditButtons(alias, dialog));

        binder.readBean(alias);
        dialog.open();
    }

    private HorizontalLayout createEditButtons(Alias data, Dialog editDialog)
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

    private void save(Alias data, Dialog editDialog)
    {
        try
        {
            binder.writeBean(data);
            aliasRepository.save(data);
            grid.setItems(aliasRepository.findAll());
            editDialog.close();

        } catch (ValidationException ex)
        {
            ex.printStackTrace();
        }
    }

    private void delete(Alias alias)
    {
        Dialog dialog = new Dialog();
        H3 sure = new H3("Are you sure you want to delete this alias?");
        Paragraph shortC = new Paragraph("Short command: " + alias.getShortCommand());
        Paragraph fullC = new Paragraph("Full command: " + alias.getFullCommand());
        dialog.add(sure, shortC, fullC, createDeleteButtons(alias, dialog));
        dialog.open();
    }

    private HorizontalLayout createDeleteButtons(Alias data, Dialog deleteDialog)
    {
        HorizontalLayout buttons = new HorizontalLayout();
        Button yes = new Button("Yes", e -> deleteButton(data, deleteDialog));
        yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button no = new Button("No", e -> deleteDialog.close());
        no.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(yes, no);
        buttons.setJustifyContentMode(JustifyContentMode.END);
        return buttons;
    }

    private void deleteButton(Alias data, Dialog deleteDialog)
    {
        aliasRepository.delete(data);
        grid.setItems(aliasRepository.findAll());
        deleteDialog.close();
    }
}
