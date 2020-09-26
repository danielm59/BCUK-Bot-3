package com.expiredminotaur.bcukbot.twitch.streams;

import com.expiredminotaur.bcukbot.discord.DiscordBot;
import com.expiredminotaur.bcukbot.sql.twitch.streams.group.Group;
import discord4j.core.object.entity.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiTwitchHandler
{
    private final DiscordBot discordBot;
    private final Map<String, MultiTwitch> multiTwitchs = new HashMap<>();

    public MultiTwitchHandler(DiscordBot discordBot)
    {
        this.discordBot = discordBot;
    }

    public void update(Map<String, StreamData> groupData, Group group)
    {
        Map<String, Set<String>> currentStreams = setupMap(groupData);

        for (String game : currentStreams.keySet())
        {
            processGame(game, group, currentStreams);
        }
    }

    private Map<String, Set<String>> setupMap(Map<String, StreamData> groupData)
    {
        Map<String, Set<String>> currentStreams = new HashMap<>();
        for (Map.Entry<String, StreamData> user : groupData.entrySet())
        {
            currentStreams.computeIfAbsent(user.getValue().getGame(), k -> new HashSet<>()).add(user.getKey());
        }
        return currentStreams;
    }

    private void processGame(String game, Group group, Map<String, Set<String>> currentStreams)
    {
        if (multiTwitchs.containsKey(game))
        {
            updateGame(game, group, currentStreams);
        } else
        {
            if (currentStreams.get(game).size() > 1)
            {
                Message message = formatAndSendMulti(group, currentStreams.get(game), game);
                multiTwitchs.put(game, new MultiTwitch(currentStreams.get(game), message, System.currentTimeMillis()));
            }
        }
    }

    private void updateGame(String game, Group group, Map<String, Set<String>> currentStreams)
    {
        MultiTwitch multiTwitch = multiTwitchs.get(game);
        if (!currentStreams.get(game).equals(multiTwitch.users))
        {
            deleteOldMessage(group, multiTwitch);
            if (currentStreams.get(game).size() > 1)
            {
                multiTwitch.message = formatAndSendMulti(group, currentStreams.get(game), game);
                multiTwitch.users = currentStreams.get(game);

            } else
            {
                multiTwitchs.remove(game);
            }
        }
        multiTwitch.lastOnline = System.currentTimeMillis();
    }

    private void deleteOldMessage(Group group, MultiTwitch multiTwitch)
    {
        if (group.isDeleteOldPosts())
        {
            multiTwitch.message.delete("Offline Stream").subscribe();
        }
    }

    private Message formatAndSendMulti(Group group, Set<String> users, String game)
    {
        String message = group.getMultiTwitchMessage();
        message = message.replace("%game%", game);
        message = message.replace("%link%", createLink(users));
        return discordBot.sendAndGetMessage(group.getDiscordChannel(), message);
    }

    private static String createLink(Set<String> users)
    {
        StringBuilder link = new StringBuilder();
        link.append("http://multitwitch.tv/");
        for (String user : users)
        {
            link.append(user);
            link.append("/");
        }
        return link.toString();
    }

    public String getMultiTwitch(String channel)
    {
        for (MultiTwitch mt : multiTwitchs.values())
        {
            if (mt.users.contains(channel))
                return createLink(mt.users);
        }
        return null;
    }

    private static class MultiTwitch
    {
        Set<String> users;
        Message message;
        Long lastOnline;

        MultiTwitch(Set<String> users, Message message, Long lastOnline)
        {
            this.users = users;
            this.message = message;
            this.lastOnline = lastOnline;
        }
    }
}
