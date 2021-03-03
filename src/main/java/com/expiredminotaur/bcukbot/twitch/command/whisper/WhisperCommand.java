package com.expiredminotaur.bcukbot.twitch.command.whisper;

import com.expiredminotaur.bcukbot.command.Command;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class WhisperCommand extends Command<WhisperCommandEvent>
{
    public WhisperCommand(Function<WhisperCommandEvent, Mono<Void>> task)
    {
        //TODO: restrict commands to an allowed list of users
        super(task, e -> true);
    }
}
