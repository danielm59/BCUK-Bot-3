package com.expiredminotaur.bcukbot.twitch.command.whisper;

import com.expiredminotaur.bcukbot.command.Command;

import java.util.function.Function;

public class WhisperCommand extends Command<WhisperCommandEvent, Void>
{
    public WhisperCommand(Function<WhisperCommandEvent, Void> task)
    {
        //TODO: restrict commands to an allowed list of users
        super(task, e -> true);
    }
}
