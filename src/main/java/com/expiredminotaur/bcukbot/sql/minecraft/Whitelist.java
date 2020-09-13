package com.expiredminotaur.bcukbot.sql.minecraft;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(catalog = "bcuk_mc")
public class Whitelist
{
    @Id
    private Long discordID;
    private String mcUUID;

    private Whitelist()
    {
    }

    public Whitelist(Long discordID, String mcUUID)
    {
        this.discordID = discordID;
        this.mcUUID = mcUUID;
    }

    public Long getDiscordID()
    {
        return discordID;
    }

    public void setDiscordID(Long discordID)
    {
        this.discordID = discordID;
    }

    public String getMcUUID()
    {
        return mcUUID;
    }

    public void setMcUUID(String mcUUID)
    {
        this.mcUUID = mcUUID;
    }
}
