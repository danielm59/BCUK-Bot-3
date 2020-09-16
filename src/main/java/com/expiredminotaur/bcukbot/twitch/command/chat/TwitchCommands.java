package com.expiredminotaur.bcukbot.twitch.command.chat;

import com.expiredminotaur.bcukbot.discord.music.MusicHandler;
import com.expiredminotaur.bcukbot.fun.counters.CounterHandler;
import com.expiredminotaur.bcukbot.fun.dadjokes.JokeAPI;
import com.expiredminotaur.bcukbot.sql.collection.joke.JokeUtils;
import com.expiredminotaur.bcukbot.sql.command.alias.Alias;
import com.expiredminotaur.bcukbot.sql.command.alias.AliasRepository;
import com.expiredminotaur.bcukbot.sql.sfx.SFXRepository;
import com.expiredminotaur.bcukbot.twitch.streams.LiveStreamManager;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

@Component
public class TwitchCommands
{
    private final TreeMap<String, TwitchCommand> commands = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    //region Autowired
    @Autowired
    private AliasRepository aliasRepository;

    @Autowired
    private SFXRepository sfxRepository;

    @Autowired
    private CounterHandler counterHandler;

    @Autowired
    private JokeUtils jokeUtils;

    @Autowired
    private MusicHandler musicHandler;

    @Autowired
    private LiveStreamManager liveStreamManager;
    //endregion

    public TwitchCommands()
    {
        commands.put("!Sfx", new TwitchCommand(e -> e.respond(sfx()), TwitchPermissions::everyone));
        commands.put("!SO", new TwitchCommand(this::shoutOut, TwitchPermissions::modPlus));
        commands.put("!DadJoke", new TwitchCommand(JokeAPI::jokeCommand, TwitchPermissions::everyone));
        commands.put("!Joke", new TwitchCommand(e -> jokeUtils.processCommand(e), TwitchPermissions::everyone));
        commands.put("!Playing", new TwitchCommand(this::playing, TwitchPermissions::everyone));
        commands.put("!Multi", new TwitchCommand(e -> liveStreamManager.getMultiTwitch(e), TwitchPermissions::everyone));
    }

    private Void shoutOut(TwitchCommandEvent e)
    {
        String[] args = e.getFinalMessage().split(" ", 2);
        if (args.length == 2)
        {
            String channel = args[1].replace("@", ""); //Remove at in case someone dose @Username
            return e.respond(
                    String.format("Go and check out %s's channel over at https://www.twitch.tv/%s",
                            channel, channel));
        }
        return null;
    }

    public Void playing(TwitchCommandEvent event)
    {
        AudioTrack track = musicHandler.getScheduler().currentTrack();
        if (track != null)
        {
            return event.respond("Playing: " + track.getInfo().title);
        } else
        {
            return event.respond("Nothing is playing");
        }
    }

    public void processCommand(ChannelMessageEvent event)
    {
        TwitchCommandEvent cEvent = new TwitchCommandEvent(event);
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
            TwitchCommand com = commands.get(command[0]);

            if (com.hasPermission(cEvent))
            {
                com.runTask(cEvent);
            }
        }

        counterHandler.processCommand(cEvent);
    }

    private String sfx()
    {
        StringBuilder s = new StringBuilder();
        Set<String> triggers = new HashSet<>();
        sfxRepository.findAll().forEach(sfx -> triggers.add(sfx.getTriggerCommand()));
        triggers.forEach(trigger -> s.append(trigger).append(", "));
        s.setLength(s.length() - 2);

        return s.toString();
    }
}
