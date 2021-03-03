package com.expiredminotaur.bcukbot.twitch.command.whisper;

import com.expiredminotaur.bcukbot.command.Commands;
import com.expiredminotaur.bcukbot.discord.music.MusicHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WhisperCommands extends Commands<WhisperCommand, WhisperCommandEvent>
{
    @Autowired
    @Lazy
    private MusicHandler musicHandler;

    public WhisperCommands()
    {
        commands.put("!volume", new WhisperCommand(this::volume));
        commands.put("!pause", new WhisperCommand(e -> musicHandler.togglePause(e)));
    }

    private Mono<Void> volume(WhisperCommandEvent event)
    {
        String content = event.getFinalMessage();

        if (!content.isEmpty())
        {
            String[] args = content.split(" ");
            if (args.length > 1)
            {
                return musicHandler.setVolume(event, args[1]);
            } else
            {
                return musicHandler.getVolume(event);
            }
        }
        return event.empty();
    }
}
