package com.expiredminotaur.bcukbot.sql.tasks;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String task;
    private boolean completed;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getTask()
    {
        return task;
    }

    public void setTask(String task)
    {
        this.task = task;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }
}
