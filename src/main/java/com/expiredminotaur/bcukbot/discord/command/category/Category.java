package com.expiredminotaur.bcukbot.discord.command.category;

import com.expiredminotaur.bcukbot.command.Commands;
import com.expiredminotaur.bcukbot.discord.command.DiscordCommandEvent;

public abstract class Category extends Commands<DiscordCommandEvent>
{
    public abstract String getName();
}