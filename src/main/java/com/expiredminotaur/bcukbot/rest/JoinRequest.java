package com.expiredminotaur.bcukbot.rest;

public class JoinRequest
{
    Long channelId;

    public JoinRequest()
    {
    }

    public JoinRequest(Long channelId)
    {
        this.channelId = channelId;
    }

    public Long getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Long channelId)
    {
        this.channelId = channelId;
    }
}
