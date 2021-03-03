package com.expiredminotaur.bcukbot.twitch.command.chat;

import com.expiredminotaur.bcukbot.command.Command;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class TwitchCommand extends Command<TwitchCommandEvent>
{
    public TwitchCommand(Function<TwitchCommandEvent, Mono<Void>> task, Function<TwitchCommandEvent, Boolean> permission)
    {
        super(task, permission);
    }
}
