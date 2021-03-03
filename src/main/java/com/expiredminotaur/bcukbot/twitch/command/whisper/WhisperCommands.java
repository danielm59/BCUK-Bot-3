package com.expiredminotaur.bcukbot.twitch.command.whisper;

import com.expiredminotaur.bcukbot.command.Commands;
import com.expiredminotaur.bcukbot.discord.command.DiscordMusicCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WhisperCommands extends Commands<WhisperCommand, WhisperCommandEvent>
{
    @Autowired
    private DiscordMusicCommands discordMusicCommands;

    public WhisperCommands()
    {
        commands.put("!volume", new WhisperCommand(discordMusicCommands::volume));
        commands.put("!pause", new WhisperCommand(discordMusicCommands::pause));
    }
}
