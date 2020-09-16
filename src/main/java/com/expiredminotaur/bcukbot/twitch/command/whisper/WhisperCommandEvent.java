package com.expiredminotaur.bcukbot.twitch.command.whisper;

import com.expiredminotaur.bcukbot.command.CommandEvent;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.common.events.user.PrivateMessageEvent;

public class WhisperCommandEvent extends CommandEvent<PrivateMessageEvent, Void>
{
    private final TwitchClient client;

    public WhisperCommandEvent(PrivateMessageEvent event, TwitchClient client)
    {
        super(event);
        this.client = client;
    }

    @Override
    public String getOriginalMessage()
    {
        return event.getMessage();
    }

    @Override
    public Void respond(String message)
    {
        client.getChat().sendPrivateMessage(event.getUser().getName(), message);
        return null;
    }

    @Override
    public Void empty()
    {
        return null;
    }
}
