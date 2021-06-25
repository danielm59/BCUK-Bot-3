package com.expiredminotaur.bcukbot.web.view.admin;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.tasks.Task;
import com.expiredminotaur.bcukbot.sql.tasks.TaskRepository;
import com.expiredminotaur.bcukbot.web.component.Form;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@AccessLevel(Role.ADMIN)
@Route(value = "/task_manager", layout = MainLayout.class)
public class TaskManagerView extends VerticalLayout
{
    private final TaskRepository tasks;
    private final Grid<Task> grid;

    public TaskManagerView(@Autowired TaskRepository tasks)
    {
        setSizeFull();
        this.tasks = tasks;
        TaskForm form = new TaskForm();
        grid = new Grid<>(Task.class);
        grid.setColumns("task", "completed");
        grid.addColumn(new ComponentRenderer<>(task -> new Button("Edit", e -> form.open(task)))).setFlexGrow(0);
        grid.setSizeFull();
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        updateGrid();
        Button addTask = new Button("Add Task", e -> form.open(new Task()));
        add(addTask, grid);
    }

    private void updateGrid()
    {
        grid.setItems(tasks.findAll());
        grid.recalculateColumnWidths();
    }

    private class TaskForm extends Form<Task>
    {
        public TaskForm()
        {
            super(Task.class);
            addField("Task", new TextField(), "task").setWidthFull();
        }

        @Override
        protected void saveData(Task data)
        {
            data.setCompleted(false);
            tasks.save(data);
            updateGrid();
        }
    }
}
