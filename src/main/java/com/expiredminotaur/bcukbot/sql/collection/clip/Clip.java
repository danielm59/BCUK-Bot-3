package com.expiredminotaur.bcukbot.sql.collection.clip;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class Clip
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String clip;
    private String source;
    private Date date;

    private Clip()
    {
    }

    public Clip(String clip, String source)
    {
        this.clip = clip;
        this.source = source;
        date = new Date(System.currentTimeMillis());
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getClip()
    {
        return clip;
    }

    public void setClip(String clip)
    {
        this.clip = clip;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
}
