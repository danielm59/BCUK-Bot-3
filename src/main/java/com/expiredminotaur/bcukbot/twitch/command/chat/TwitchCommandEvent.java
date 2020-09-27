package com.expiredminotaur.bcukbot.twitch.command.chat;


import com.expiredminotaur.bcukbot.command.CommandEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class TwitchCommandEvent extends CommandEvent<ChannelMessageEvent, Void>
{

    public TwitchCommandEvent(ChannelMessageEvent event)
    {
        super(event);
    }

    @Override
    public String getOriginalMessage()
    {
        return event.getMessage();
    }

    public Void respond(String message)
    {
        event.getTwitchChat().sendMessage(event.getChannel().getName(), message);
        return null;
    }

    public Void empty()
    {
        return null;
    }

    @Override
    public String getSourceName()
    {
        return "Twitch_" + event.getChannel().getName();
    }
}
