package com.expiredminotaur.bcukbot.discord.command;

import com.expiredminotaur.bcukbot.command.Command;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class DiscordCommand extends Command<DiscordCommandEvent>
{
    public DiscordCommand(Function<DiscordCommandEvent, Mono<Void>> task, Function<DiscordCommandEvent, Boolean> permission)
    {
        super(task, permission);
    }
}
