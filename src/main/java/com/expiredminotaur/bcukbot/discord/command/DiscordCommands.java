package com.expiredminotaur.bcukbot.discord.command;

import com.expiredminotaur.bcukbot.discord.PointsSystem;
import com.expiredminotaur.bcukbot.fun.counters.CounterHandler;
import com.expiredminotaur.bcukbot.fun.dadjokes.JokeAPI;
import com.expiredminotaur.bcukbot.fun.slot.SlotGame;
import com.expiredminotaur.bcukbot.fun.trivia.TriviaGame;
import com.expiredminotaur.bcukbot.justgiving.JustGivingAPI;
import com.expiredminotaur.bcukbot.sql.collection.clip.ClipUtils;
import com.expiredminotaur.bcukbot.sql.collection.joke.JokeUtils;
import com.expiredminotaur.bcukbot.sql.collection.quote.QuoteUtils;
import com.expiredminotaur.bcukbot.sql.command.alias.Alias;
import com.expiredminotaur.bcukbot.sql.command.alias.AliasRepository;
import com.expiredminotaur.bcukbot.sql.sfx.SFXRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public class DiscordCommands
{
    private final TreeMap<DiscordCommandCategory, Map<String, DiscordCommand>> commands = new TreeMap<>();

    //region Autowired
    @Autowired
    private QuoteUtils quoteUtils;

    @Autowired
    private JokeUtils jokeUtils;

    @Autowired
    private ClipUtils clipUtils;

    @Autowired
    private AliasRepository discordAliasRepository;

    @Autowired
    private CounterHandler counterHandler;

    @Autowired
    private SFXRepository sfxRepository;

    @Autowired
    private DiscordMusicCommands musicCommands;

    @Autowired
    private PointsSystem pointsSystem;

    @Autowired
    private SlotGame slotGame;

    @Autowired
    private TriviaGame triviaGame;

    @Autowired
    private MinecraftCommands minecraftCommands;

    @Autowired
    private JustGivingAPI justGivingAPI;
    //endregion

    public DiscordCommands()
    {
        for (DiscordCommandCategory category : DiscordCommandCategory.values())
        {
            commands.put(category, new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        }

        commands.get(DiscordCommandCategory.GENERAL).put("!Commands", new DiscordCommand(this::commands, DiscordPermissions::general));

        commands.get(DiscordCommandCategory.GENERAL).put("!Quote", new DiscordCommand(e -> quoteUtils.processCommand(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.GENERAL).put("!Clip", new DiscordCommand(e -> clipUtils.processCommand(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.GENERAL).put("!Joke", new DiscordCommand(e -> jokeUtils.processCommand(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.GENERAL).put("!GameBlastTotal", new DiscordCommand(e->justGivingAPI.amountRaised(e), DiscordPermissions::general));

        commands.get(DiscordCommandCategory.GAMES).put("!Points", new DiscordCommand(e -> pointsSystem.points(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.GAMES).put("!Slot", new DiscordCommand(e -> slotGame.startGame(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.GAMES).put("!Trivia", new DiscordCommand(e -> triviaGame.trivia(e), DiscordPermissions::general));

        commands.get(DiscordCommandCategory.MUSIC).put("!Join", new DiscordCommand(e -> musicCommands.join(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.MUSIC).put("!Play", new DiscordCommand(e -> musicCommands.play(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.MUSIC).put("!YT", new DiscordCommand(e -> musicCommands.playYT(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.MUSIC).put("!Skip", new DiscordCommand(e -> musicCommands.skip(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.MUSIC).put("!Stop", new DiscordCommand(e -> musicCommands.stop(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.MUSIC).put("!List", new DiscordCommand(e -> musicCommands.list(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.MUSIC).put("!Playing", new DiscordCommand(e -> musicCommands.playing(e), DiscordPermissions::general));

        commands.get(DiscordCommandCategory.MUSIC).put("!Volume", new DiscordCommand(e -> musicCommands.volume(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.MUSIC).put("!Pause", new DiscordCommand(e -> musicCommands.pause(e), DiscordPermissions::general));
        commands.get(DiscordCommandCategory.MUSIC).put("!Leave", new DiscordCommand(e -> musicCommands.leave(e), DiscordPermissions::general));

        commands.get(DiscordCommandCategory.GENERAL).put("!DadJoke", new DiscordCommand(JokeAPI::jokeCommand, DiscordPermissions::general));

        commands.get(DiscordCommandCategory.GENERAL).put("!SFX", new DiscordCommand(this::sfx, DiscordPermissions::general));

        commands.get(DiscordCommandCategory.MINECRAFT).put("!Whitelist", new DiscordCommand(e->minecraftCommands.whitelist(e), event -> DiscordPermissions.hasRole(event, Snowflake.of(489887389725229066L))));
    }

    public Mono<Void> processCommand(MessageCreateEvent event)
    {
        DiscordCommandEvent cEvent = new DiscordCommandEvent(event);
        Message message = event.getMessage();
        String[] command = message.getContent().split(" ", 2);

        List<Alias> alias = discordAliasRepository.findByTrigger(command[0]);
        if (alias.size() > 0)
        {
            String newCommand = alias.get(0).getFullCommand();
            command = newCommand.split(" ", 2);
            cEvent.setAliased(newCommand);
        }

        for (Map<String, DiscordCommand> category : commands.values())
        {
            if (category.containsKey(command[0]))
            {
                DiscordCommand com = category.get(command[0]);

                if (com.hasPermission(cEvent))
                {
                    return com.runTask(cEvent).onErrorResume(e -> Mono.empty());
                } else
                {
                    return message.getChannel().flatMap(c -> c.createMessage("You can't do that!")).then();
                }
            }
        }
        return counterHandler.processCommand(cEvent).then();
    }

    private Mono<Void> sfx(DiscordCommandEvent event)
    {
        StringBuilder s = new StringBuilder();
        Set<String> triggers = new HashSet<>();
        sfxRepository.getSFXList().forEach(sfx -> triggers.add(sfx.getTriggerCommand()));
        triggers.forEach(trigger -> s.append(trigger).append(", "));
        s.setLength(s.length() - 2);

        return event.getEvent().getMessage().getChannel().flatMap(
                channel -> channel.createMessage(s.toString())).then();
    }


    private Mono<Void> commands(DiscordCommandEvent event)
    {
        StringBuilder s = new StringBuilder();
        s.append("```\nAvailable commands:\n");
        for (DiscordCommandCategory category : commands.keySet())
        {
            s.append(String.format("\n%s\n", category.toString()));
            s.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

            for (String key : commands.get(category).keySet())
            {
                DiscordCommand command = commands.get(category).get(key);
                if (command.hasPermission(event))
                    s.append(String.format("%s\n", key));
            }
        }
        s.append("```");
        return event.getEvent().getMessage().getChannel().flatMap(channel -> channel.createMessage(s.toString())).then();
    }
}
