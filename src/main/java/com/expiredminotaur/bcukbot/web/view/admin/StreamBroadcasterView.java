package com.expiredminotaur.bcukbot.web.view.admin;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.tasks.Punishment;
import com.expiredminotaur.bcukbot.sql.tasks.PunishmentRepository;
import com.expiredminotaur.bcukbot.sql.tasks.Task;
import com.expiredminotaur.bcukbot.sql.tasks.TaskRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.expiredminotaur.bcukbot.web.view.stream.StreamView.Service.broadcast;

@AccessLevel(Role.ADMIN)
@Route(value = "/stream_broadcaster", layout = MainLayout.class)
public class StreamBroadcasterView extends VerticalLayout
{
    private static final Random rand = new Random();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static String lastMessage = "";

    private final TaskRepository tasks;
    private final PunishmentRepository punishments;

    private final Paragraph info;
    private final Paragraph currentTask;
    private final Paragraph lastMessageDisplay;
    private final Button newTaskButton;
    private final Button completeTaskButton;
    private final Button failTaskButton;
    private final Button punishmentButton;

    private static Task cTask = null;

    public StreamBroadcasterView(@Autowired TaskRepository tasks, @Autowired PunishmentRepository punishments)
    {
        this.tasks = tasks;
        this.punishments = punishments;
        TextField input = new TextField();
        Button sendButton = new Button("Send Message", e -> sendBroadcast(input.getValue()));
        info = new Paragraph();
        currentTask = new Paragraph();
        lastMessageDisplay = new Paragraph("Last Message: " + lastMessage);
        newTaskButton = new Button("New Task", e -> selectTask());
        completeTaskButton = new Button("Complete Task", e -> completeTask());
        failTaskButton = new Button("Fail Task", e -> failTask());
        punishmentButton = new Button("Punishment", e -> selectPunishment());
        add(input, sendButton, info, currentTask, lastMessageDisplay, newTaskButton, completeTaskButton, failTaskButton, punishmentButton);
        updateButtonStates();
    }

    private void selectTask()
    {
        List<Task> tasksToDo = tasks.findByCompleted(false);
        int size = tasksToDo.size();
        if (size > 0)
        {
            int taskIdx = rand.nextInt(size);
            cTask = tasksToDo.get(taskIdx);
            sendBroadcast(cTask.getTask());
        } else
            sendBroadcast("No Tasks Available");
        updateButtonStates();
    }

    private void completeTask()
    {
        cTask.setCompleted(true);
        tasks.save(cTask);
        cTask = null;
        sendBroadcast("Task Complete");
        updateButtonStates();
    }

    private void failTask()
    {
        cTask = null;
        sendBroadcast("Task Failed: Punishment Time!");
        updateButtonStates();
    }

    private void selectPunishment()
    {
        List<Punishment> punishmentOptions = punishments.findByPunishmentGiven(false);
        int size = punishmentOptions.size();
        if (size > 0)
        {
            int punishmentIdx = rand.nextInt(size);
            Punishment cPunishment = punishmentOptions.get(punishmentIdx);
            sendBroadcast(cPunishment.getPunishment());
            cPunishment.setPunishmentGiven(true);
            punishments.save(cPunishment);
        } else
            sendBroadcast("No Punishments Available");
        updateButtonStates();
    }

    private void updateButtonStates()
    {
        newTaskButton.setEnabled(false);
        completeTaskButton.setEnabled(false);
        failTaskButton.setEnabled(false);
        punishmentButton.setEnabled(false);

        if (cTask == null)
        {
            newTaskButton.setEnabled(true);
            punishmentButton.setEnabled(true);
            currentTask.setText("No Task Active");
        } else
        {
            completeTaskButton.setEnabled(true);
            failTaskButton.setEnabled(true);
            currentTask.setText("Active Task: " + cTask.getTask());
        }
        info.setText(String.format("Tasks Available = %d ---- Punishments Available = %d",
                tasks.findByCompleted(false).size(),
                punishments.findByPunishmentGiven(false).size()));
    }

    private void sendBroadcast(String message)
    {
        executor.submit(() -> broadcast(message));
        lastMessage = message;
        lastMessageDisplay.setText("Last Message: " + lastMessage);
    }
}
