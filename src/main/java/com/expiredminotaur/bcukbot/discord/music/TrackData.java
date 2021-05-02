package com.expiredminotaur.bcukbot.discord.music;

public class TrackData
{
    private String requestedBy = null;
    private Boolean sfx = false;

    public String getRequestedBy()
    {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy)
    {
        this.requestedBy = requestedBy;
    }

    public Boolean isSfx()
    {
        return sfx;
    }

    public void setSfx(Boolean sfx)
    {
        this.sfx = sfx;
    }
}
