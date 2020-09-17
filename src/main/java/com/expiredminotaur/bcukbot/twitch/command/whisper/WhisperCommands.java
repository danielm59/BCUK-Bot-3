package com.expiredminotaur.bcukbot.twitch.command.whisper;

import com.expiredminotaur.bcukbot.discord.music.MusicHandler;
import com.expiredminotaur.bcukbot.sql.command.alias.Alias;
import com.expiredminotaur.bcukbot.sql.command.alias.AliasRepository;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.common.events.user.PrivateMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;

@Component
public class WhisperCommands
{
    private final TreeMap<String, WhisperCommand> commands = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Autowired
    private AliasRepository aliasRepository;

    @Autowired
    @Lazy
    private MusicHandler musicHandler;

    public WhisperCommands()
    {
        commands.put("!volume", new WhisperCommand(this::volume));
        commands.put("!pause", new WhisperCommand(e -> musicHandler.togglePause(e)));
    }

    private Void volume(WhisperCommandEvent event)
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

    public void processCommand(PrivateMessageEvent event, TwitchClient client)
    {
        WhisperCommandEvent cEvent = new WhisperCommandEvent(event, client);
        String message = event.getMessage();
        String[] command = message.split(" ", 2);

        List<Alias> alias = aliasRepository.findByTrigger(command[0]);
        if (alias.size() > 0)
        {
            String newCommand = alias.get(0).getFullCommand();
            command = newCommand.split(" ", 2);
            cEvent.setAliased(newCommand);
        }
        if (commands.containsKey(command[0]))
        {
            WhisperCommand com = commands.get(command[0]);

            if (com.hasPermission(cEvent))
            {
                com.runTask(cEvent);
            }
        }
    }
}
