package com.expiredminotaur.bcukbot.sql.twitch.cache.user;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TwitchUser
{
    @Id
    String userID;
    String userName;

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
