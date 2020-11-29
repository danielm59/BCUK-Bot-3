package com.expiredminotaur.bcukbot.sql.twitch.bannedphrase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BannedPhrase
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String phrase;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPhrase()
    {
        return phrase;
    }

    public void setPhrase(String phrase)
    {
        this.phrase = phrase;
    }
}
