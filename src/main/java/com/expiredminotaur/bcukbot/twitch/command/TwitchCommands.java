package com.expiredminotaur.bcukbot.twitch.command;

import com.expiredminotaur.bcukbot.fun.counters.CounterHandler;
import com.expiredminotaur.bcukbot.fun.dadjokes.JokeAPI;
import com.expiredminotaur.bcukbot.sql.collection.joke.JokeUtils;
import com.expiredminotaur.bcukbot.sql.command.alias.Alias;
import com.expiredminotaur.bcukbot.sql.command.alias.AliasRepository;
import com.expiredminotaur.bcukbot.sql.sfx.SFXRepository;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

@Component
public class TwitchCommands extends TreeMap<String, TwitchCommand>
{
    //region Autowired
    @Autowired
    private TwitchCommands commands;

    @Autowired
    private AliasRepository aliasRepository;

    @Autowired
    private SFXRepository sfxRepository;

    @Autowired
    private CounterHandler counterHandler;

    @Autowired
    private JokeUtils jokeUtils;
    //endregion

    public TwitchCommands()
    {
        super(String.CASE_INSENSITIVE_ORDER);
        this.put("!Sfx", new TwitchCommand(e -> e.respond(sfx()), TwitchPermissions::everyone));
        this.put("!SO", new TwitchCommand(this::shoutOut, TwitchPermissions::modPlus));
        this.put("!DadJoke", new TwitchCommand(JokeAPI::jokeCommand, TwitchPermissions::everyone));
        this.put("!Joke", new TwitchCommand(e -> jokeUtils.processCommand(e), TwitchPermissions::everyone));

        /*
        this.put("!Multi", new Command(e -> LiveStreams.postMultiTwitch(c)));
        this.put("!Playing", new Command(e -> playing(c)));
         */
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
