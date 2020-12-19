package com.expiredminotaur.bcukbot.sql.twitch.streams.streamer;

import com.expiredminotaur.bcukbot.sql.twitch.streams.group.Group;

import javax.persistence.*;

@Entity
public class Streamer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Group getGroup()
    {
        return group;
    }

    public void setGroup(Group group)
    {
        this.group = group;
    }
}
