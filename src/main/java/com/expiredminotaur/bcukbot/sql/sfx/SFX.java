package com.expiredminotaur.bcukbot.sql.sfx;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SFX
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String triggerCommand;
    String file;
    int weight = 1;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTriggerCommand()
    {
        return triggerCommand;
    }

    public void setTriggerCommand(String triggerCommand)
    {
        this.triggerCommand = triggerCommand;
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }
}
