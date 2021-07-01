package com.expiredminotaur.bcukbot.web.view.admin;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.tasks.Punishment;
import com.expiredminotaur.bcukbot.sql.tasks.PunishmentRepository;
import com.expiredminotaur.bcukbot.sql.tasks.Task;
import com.expiredminotaur.bcukbot.sql.tasks.TaskRepository;
import com.expiredminotaur.bcukbot.web.component.Form;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@AccessLevel(Role.ADMIN)
@Route(value = "/task_manager", layout = MainLayout.class)
public class TaskManagerView extends HorizontalLayout
{
    private final TaskRepository tasks;
    private final PunishmentRepository punishments;
    private final Grid<Task> taskGrid;
    private final Grid<Punishment> punishmentGrid;

    public TaskManagerView(@Autowired TaskRepository tasks, @Autowired PunishmentRepository punishments)
    {
        setSizeFull();
        this.tasks = tasks;
        this.punishments = punishments;
        TaskForm taskForm = new TaskForm();
        PunishmentForm punishmentForm = new PunishmentForm();

        taskGrid = new Grid<>(Task.class);
        taskGrid.setColumns("task", "completed");
        taskGrid.addColumn(new ComponentRenderer<>(task -> new Button("Edit", e -> taskForm.open(task)))).setFlexGrow(0);
        taskGrid.setSizeFull();
        taskGrid.getColumns().forEach(c -> c.setAutoWidth(true));
        updateTaskGrid();
        Button addTask = new Button("Add Task", e -> taskForm.open(new Task()));
        VerticalLayout taskLayout = new VerticalLayout();
        taskLayout.add(addTask, taskGrid);

        punishmentGrid = new Grid<>(Punishment.class);
        punishmentGrid.setColumns("punishment", "punishmentGiven");
        punishmentGrid.addColumn(new ComponentRenderer<>(punishment -> new Button("Edit", e -> punishmentForm.open(punishment)))).setFlexGrow(0);
        punishmentGrid.setSizeFull();
        punishmentGrid.getColumns().forEach(c -> c.setAutoWidth(true));
        updatePunishmentGrid();
        Button addPunishment = new Button("Add Punishment", e -> punishmentForm.open(new Punishment()));
        VerticalLayout punishmentLayout = new VerticalLayout();
        punishmentLayout.add(addPunishment, punishmentGrid);


        add(taskLayout, punishmentLayout);
    }

    private void updateTaskGrid()
    {
        taskGrid.setItems(tasks.findAll());
        taskGrid.recalculateColumnWidths();
    }

    private void updatePunishmentGrid()
    {
        punishmentGrid.setItems(punishments.findAll());
        punishmentGrid.recalculateColumnWidths();
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
            tasks.save(data);
            updateTaskGrid();
        }
    }

    private class PunishmentForm extends Form<Punishment>
    {
        public PunishmentForm()
        {
            super(Punishment.class);
            addField("Punishment", new TextField(), "punishment").setWidthFull();
        }

        @Override
        protected void saveData(Punishment data)
        {
            punishments.save(data);
            updatePunishmentGrid();
        }
    }
}
