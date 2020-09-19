package com.expiredminotaur.bcukbot.twitch.streams;

import com.expiredminotaur.bcukbot.discord.DiscordBot;
import com.expiredminotaur.bcukbot.sql.twitch.streams.group.Group;
import com.expiredminotaur.bcukbot.twitch.TwitchBot;
import com.github.twitch4j.helix.domain.Stream;
import discord4j.core.object.entity.Message;

import java.util.concurrent.TimeUnit;

public class StreamData
{
    private final TwitchBot twitchBot;
    private final DiscordBot discordBot;
    private Stream stream;
    private long lastUpdated;
    private Message discordMessage;

    public StreamData(TwitchBot twitchBot, DiscordBot discordBot)
    {
        this.twitchBot = twitchBot;
        this.discordBot = discordBot;
        lastUpdated = System.currentTimeMillis();
    }

    public void update(Group group, Stream newStream)
    {
        if (stream == null)
        {
            stream = newStream;
            discordMessage = discordBot.sendAndGetMessage(group.getDiscordChannel(), getLiveMessage(group));
        } else
        {
            //TODO handle issue when game id is null
            if (!stream.getGameId().equals(newStream.getGameId()))
            {
                stream = newStream;
                if (group.isDeleteOldPosts())
                {
                    discordMessage.delete("Old Stream Message").subscribe();
                }
                discordMessage = discordBot.sendAndGetMessage(group.getDiscordChannel(), getNewGameMessage(group));
            }
        }
        lastUpdated = System.currentTimeMillis();
    }

    public boolean checkValid(Group group)
    {
        if (System.currentTimeMillis() - lastUpdated > TimeUnit.MINUTES.toMillis(5))
        {
            if (group.isDeleteOldPosts())
            {
                discordMessage.delete("Old Stream Message").subscribe();
            }
            return false;
        }
        return true;
    }

    private String getLiveMessage(Group group)
    {
        String message = group.getLiveMessage();
        return replacePlaceholders(message);
    }

    private String getNewGameMessage(Group group)
    {
        String message = group.getNewGameMessage();
        return replacePlaceholders(message);
    }

    private String replacePlaceholders(String message)
    {
        message = message.replace("%channel%", stream.getUserName());
        message = message.replace("%game%", getGame());
        message = message.replace("%link%", String.format("https://www.twitch.tv/%s", stream.getUserName().toLowerCase()));
        return message;
    }

    public String getGame()
    {
        return twitchBot.getGameName(stream.getGameId());
    }

    public DiscordBot getDiscordBot()
    {
        return discordBot;
    }
}
