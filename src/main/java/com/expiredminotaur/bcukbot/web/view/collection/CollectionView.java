package com.expiredminotaur.bcukbot.web.view.collection;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.web.component.Form;
import com.expiredminotaur.bcukbot.web.security.UserTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public abstract class CollectionView<T> extends VerticalLayout
{
    private final Grid<T> grid;
    private final UserTools userTools;
    private final CrudRepository<T, Integer> repository;
    private final EditForm editForm;
    private String dataField;
    private String label;

    public CollectionView(UserTools userTools, CrudRepository<T, Integer> repository, Class<T> type)
    {
        this.userTools = userTools;
        this.repository = repository;
        this.grid = new Grid<>(type);
        this.editForm = new EditForm(type);

    }

    protected void setup(String title, String dataField, String label)
    {
        this.dataField = dataField;
        this.label = label;
        setSizeFull();
        H2 header = new H2(title);
        grid.setColumns("id", dataField, "source", "date");
        grid.setSizeFull();

        if (userTools.hasAccess(Role.MOD))
        {
            grid.addColumn(new ComponentRenderer<>(data -> new Button("Edit", e -> editForm.open(data))))
                    .setHeader("Edit")
                    .setFlexGrow(0);
        }

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.recalculateColumnWidths();

        grid.setItems((ArrayList<T>) repository.findAll());

        add(header, grid);
    }

    private class EditForm extends Form<T>
    {
        public EditForm(Class<T> type)
        {
            super(type);
            addField(label, new TextField(), dataField).setWidthFull();
        }

        @Override
        protected void saveData(T data)
        {
            repository.save(data);
            grid.getDataProvider().refreshItem(data);
        }
    }
}
