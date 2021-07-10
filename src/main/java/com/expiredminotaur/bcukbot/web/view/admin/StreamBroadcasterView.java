package com.expiredminotaur.bcukbot.web.view.admin;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.discord.music.MusicHandler;
import com.expiredminotaur.bcukbot.fun.tasks.TaskManager;
import com.expiredminotaur.bcukbot.sql.tasks.Punishment;
import com.expiredminotaur.bcukbot.sql.tasks.Task;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@AccessLevel(Role.ADMIN)
@Route(value = "/stream_broadcaster", layout = MainLayout.class)
public class StreamBroadcasterView extends VerticalLayout
{
    private final TaskManager taskManager;
    private final MusicHandler musicHandler;

    private final Paragraph info;
    private final Paragraph currentTask;
    private final Paragraph lastMessageDisplay;
    private final Button newTaskButton;
    private final Button completeTaskButton;
    private final Button failTaskButton;
    private final Button punishmentButton;
    private final Button finishPunishmentButton;

    public StreamBroadcasterView(@Autowired TaskManager taskManager, @Autowired MusicHandler musicHandler)
    {
        this.taskManager = taskManager;
        this.musicHandler = musicHandler;
        TextField input = new TextField();
        Button sendButton = new Button("Send Message", e -> sendBroadcast(input.getValue()));
        info = new Paragraph();
        currentTask = new Paragraph();
        lastMessageDisplay = new Paragraph("Last Message: " + taskManager.getLastMessage());
        Button taskWarningButton = new Button("Task Warning", e -> playTaskWarning());
        newTaskButton = new Button("New Task", e -> selectTask());
        completeTaskButton = new Button("Complete Task", e -> completeTask());
        failTaskButton = new Button("Fail Task", e -> failTask());
        punishmentButton = new Button("Punishment", e -> selectPunishment());
        finishPunishmentButton = new Button("Finish Punishment", e -> finishPunishment());
        add(input, sendButton, info, currentTask, lastMessageDisplay, taskWarningButton, newTaskButton, completeTaskButton, failTaskButton, punishmentButton, finishPunishmentButton);
        updateButtonStates();
    }

    private void playTaskWarning()
    {
        musicHandler.loadAndPlayPriority("klaxon.mp3");
    }

    private void selectTask()
    {
        Task task = taskManager.newTask();
        if (task != null)
        {
            sendBroadcast(task.getTask());
        } else
            sendBroadcast("No Tasks Available");
        updateButtonStates();
    }

    private void completeTask()
    {
        taskManager.completeTask();
        sendBroadcast("Task Complete");
        updateButtonStates();
    }

    private void failTask()
    {
        taskManager.clearTask();
        sendBroadcast("Task Failed: Punishment Time!");
        updateButtonStates();
    }

    private void selectPunishment()
    {
        Punishment punishment = taskManager.newPunishment();
        if (punishment != null)
        {
            sendBroadcast(punishment.getPunishment());
        } else
            sendBroadcast("No Punishments Available");
        updateButtonStates();
    }

    private void finishPunishment()
    {
        taskManager.finishPunishment();
        updateButtonStates();
    }

    private void updateButtonStates()
    {
        newTaskButton.setEnabled(false);
        completeTaskButton.setEnabled(false);
        failTaskButton.setEnabled(false);
        punishmentButton.setEnabled(false);
        finishPunishmentButton.setEnabled(false);

        if (taskManager.getTask() == null)
        {
            newTaskButton.setEnabled(true);
            punishmentButton.setEnabled(true);
            currentTask.setText("No Task Active");
        } else if (taskManager.getPunishment() != null)
        {
            finishPunishmentButton.setEnabled(true);
            currentTask.setText("Active punishment: " + taskManager.getPunishment().getPunishment());
        } else
        {
            completeTaskButton.setEnabled(true);
            failTaskButton.setEnabled(true);
            currentTask.setText("Active Task: " + taskManager.getTask().getTask());
        }
        info.setText(String.format("Tasks Available = %d ---- Punishments Available = %d",
                taskManager.availableTasks(),
                taskManager.availablePunishments()));
    }

    public void sendBroadcast(String message)
    {
        taskManager.broadcast(message);
        lastMessageDisplay.setText("Last Message: " + message);
    }
}
