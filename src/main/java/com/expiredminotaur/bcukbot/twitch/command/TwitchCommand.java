package com.expiredminotaur.bcukbot.twitch.command;

import com.expiredminotaur.bcukbot.command.Command;

import java.util.function.Function;

public class TwitchCommand extends Command<TwitchCommandEvent, Void>
{
    public TwitchCommand(Function<TwitchCommandEvent, Void> task, Function<TwitchCommandEvent, Boolean> permission)
    {
        super(task, permission);
    }
}
