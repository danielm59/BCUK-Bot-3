package com.expiredminotaur.bcukbot.web.view.commands;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.command.alias.Alias;
import com.expiredminotaur.bcukbot.sql.command.alias.AliasRepository;
import com.expiredminotaur.bcukbot.web.component.Form;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "alias", layout = MainLayout.class)
@AccessLevel(Role.MOD)
public class AliasView extends VerticalLayout
{
    private final AliasRepository aliasRepository;
    private final Grid<Alias> grid = new Grid<>(Alias.class);

    public AliasView(@Autowired AliasRepository aliasRepository)
    {
        this.aliasRepository = aliasRepository;
        setSizeFull();
        EditForm editForm = new EditForm();
        Button addButton = new Button("Add", e -> editForm.open(new Alias()));
        grid.setColumns("shortCommand", "fullCommand");
        grid.setItems(aliasRepository.findAll());
        grid.addColumn(new ComponentRenderer<>(alias -> new Button("Edit", e -> editForm.open(alias))))
                .setHeader("Edit")
                .setFlexGrow(0);
        grid.addColumn(new ComponentRenderer<>(alias -> new Button("Delete", e -> delete(alias))))
                .setHeader("Delete")
                .setFlexGrow(0);
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        add(addButton, grid);
    }

    private class EditForm extends Form<Alias>
    {
        public EditForm()
        {
            super(Alias.class);

            TextField shortC = addField("Short Command", new TextField(), "shortCommand");
            shortC.setWidthFull();
            shortC.setRequired(true);

            TextField fullC = addField("Full Command", new TextField(), "fullCommand");
            fullC.setWidthFull();
            fullC.setRequired(true);
        }

        @Override
        protected void saveData(Alias data)
        {
            aliasRepository.save(data);
            grid.setItems(aliasRepository.findAll());
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
