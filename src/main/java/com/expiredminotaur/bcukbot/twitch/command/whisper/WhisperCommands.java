package com.expiredminotaur.bcukbot.twitch.command.whisper;

import com.expiredminotaur.bcukbot.command.Commands;
import com.expiredminotaur.bcukbot.discord.command.DiscordMusicCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WhisperCommands extends Commands<WhisperCommandEvent>
{
    @Autowired
    private DiscordMusicCommands discordMusicCommands;

    public WhisperCommands()
    {
        commands.put("!volume", new WhisperCommand(e -> discordMusicCommands.volume(e)));
        commands.put("!pause", new WhisperCommand(e -> discordMusicCommands.pause(e)));
    }
}
