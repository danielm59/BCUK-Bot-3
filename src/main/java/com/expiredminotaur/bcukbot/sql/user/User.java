package com.expiredminotaur.bcukbot.sql.user;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User
{
    @Id
    private Long discordId;
    private String discordName;
    private String twitchName;
    private String twitchOAuth;
    private boolean isAdmin = false;
    private boolean isTwitchBotEnabled = false;

    /**
     * Only to be used by the database
     */
    private User()
    {
    }

    public User(Long discordId)
    {
        this.discordId = discordId;
    }

    public Long getDiscordId()
    {
        return discordId;
    }

    public void setDiscordId(Long discordId)
    {
        this.discordId = discordId;
    }

    public String getDiscordName()
    {
        return discordName;
    }

    public void setDiscordName(String discordName)
    {
        this.discordName = discordName;
    }

    public String getTwitchName()
    {
        return twitchName;
    }

    public void setTwitchName(String twitchName)
    {
        this.twitchName = twitchName;
    }

    public String getTwitchOAuth()
    {
        return twitchOAuth;
    }

    public void setTwitchOAuth(String twitchOAuth)
    {
        this.twitchOAuth = twitchOAuth;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }

    public boolean isTwitchBotEnabled()
    {
        return isTwitchBotEnabled;
    }

    public void setTwitchBotEnabled(boolean twitchBotEnabled)
    {
        isTwitchBotEnabled = twitchBotEnabled;
    }
}
