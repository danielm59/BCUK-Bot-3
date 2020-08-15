package com.expiredminotaur.bcukbot.discord.command;

import com.expiredminotaur.bcukbot.command.CommandEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class DiscordCommandEvent extends CommandEvent<MessageCreateEvent, Mono<Void>>
{

    public DiscordCommandEvent(MessageCreateEvent event)
    {
        super(event);
    }

    @Override
    public String getOriginalMessage()
    {
        return event.getMessage().getContent();
    }

    @Override
    public Mono<Void> respond(String message)
    {
        return event.getMessage().getChannel().flatMap(c -> c.createMessage(message)).then();
    }

    @Override
    public Mono<Void> empty()
    {
        return Mono.empty();
    }
}
