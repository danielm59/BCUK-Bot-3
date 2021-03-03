package com.expiredminotaur.bcukbot.discord.command.category;

import com.expiredminotaur.bcukbot.discord.command.DiscordCommand;
import com.expiredminotaur.bcukbot.discord.command.DiscordMusicCommands;
import com.expiredminotaur.bcukbot.discord.command.DiscordPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MusicCategory extends Category
{
    private DiscordMusicCommands musicCommands;

    public MusicCategory()
    {
        commands.put("!Join", new DiscordCommand(e -> musicCommands.join(e), DiscordPermissions::general));
        commands.put("!Play", new DiscordCommand(e -> musicCommands.play(e), DiscordPermissions::general));
        commands.put("!YT", new DiscordCommand(e -> musicCommands.playYT(e), DiscordPermissions::general));
        commands.put("!Skip", new DiscordCommand(e -> musicCommands.skip(e), DiscordPermissions::general));
        commands.put("!Stop", new DiscordCommand(e -> musicCommands.stop(e), DiscordPermissions::general));
        commands.put("!List", new DiscordCommand(e -> musicCommands.list(e), DiscordPermissions::general));
        commands.put("!Playing", new DiscordCommand(e -> musicCommands.playing(e), DiscordPermissions::general));
        commands.put("!Volume", new DiscordCommand(e -> musicCommands.volume(e), DiscordPermissions::general));
        commands.put("!Pause", new DiscordCommand(e -> musicCommands.pause(e), DiscordPermissions::general));
        commands.put("!Leave", new DiscordCommand(e -> musicCommands.leave(e), DiscordPermissions::general));
    }

    @Autowired
    public final void setMusicCommands(DiscordMusicCommands musicCommands)
    {
        this.musicCommands = musicCommands;
    }

    @Override
    public String getName()
    {
        return "MUSIC";
    }
}
