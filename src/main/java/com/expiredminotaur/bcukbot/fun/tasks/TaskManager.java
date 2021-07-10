package com.expiredminotaur.bcukbot.fun.tasks;

import com.expiredminotaur.bcukbot.sql.tasks.Punishment;
import com.expiredminotaur.bcukbot.sql.tasks.PunishmentRepository;
import com.expiredminotaur.bcukbot.sql.tasks.Task;
import com.expiredminotaur.bcukbot.sql.tasks.TaskRepository;
import com.expiredminotaur.bcukbot.web.view.stream.StreamView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TaskManager
{

    private static final Random rand = new Random();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static String lastMessage = "";

    private final TaskRepository tasks;
    private final PunishmentRepository punishments;

    private Task cTask = null;
    private Punishment cPunishment = null;

    public TaskManager(@Autowired TaskRepository tasks, @Autowired PunishmentRepository punishments)
    {
        this.tasks = tasks;
        this.punishments = punishments;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void completeTask()
    {
        cTask.setCompleted(true);
        tasks.save(cTask);
        cTask = null;
    }

    public void clearTask()
    {
        cTask = null;
    }

    public Task getTask()
    {
        return cTask;
    }

    public Punishment getPunishment()
    {
        return cPunishment;
    }

    public Task newTask()
    {
        List<Task> tasksToDo = tasks.findByCompleted(false);
        int size = tasksToDo.size();
        cTask = null;
        if (size > 0)
        {
            int taskIdx = rand.nextInt(size);
            cTask = tasksToDo.get(taskIdx);
        }
        return cTask;
    }

    public Punishment newPunishment()
    {
        List<Punishment> punishmentsAvailable = punishments.findByPunishmentGiven(false);
        int size = punishmentsAvailable.size();
        cPunishment = null;
        if (size > 0)
        {
            int taskIdx = rand.nextInt(size);
            cPunishment = punishmentsAvailable.get(taskIdx);
        }
        return cPunishment;
    }

    public void finishPunishment()
    {
        if (cPunishment != null)
        {
            cPunishment.setPunishmentGiven(true);
            punishments.save(cPunishment);
            cPunishment = null;
        }
    }

    public void broadcast(String message)
    {
        executor.submit(() -> StreamView.Service.broadcast(message));
        lastMessage = message;
    }

    public int availableTasks()
    {
        return tasks.findByCompleted(false).size();
    }

    public int availablePunishments()
    {
        return punishments.findByPunishmentGiven(false).size();
    }

}
