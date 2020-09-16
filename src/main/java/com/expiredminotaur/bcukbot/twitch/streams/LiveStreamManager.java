package com.expiredminotaur.bcukbot.twitch.streams;

import com.expiredminotaur.bcukbot.discord.DiscordBot;
import com.expiredminotaur.bcukbot.sql.twitch.streams.group.Group;
import com.expiredminotaur.bcukbot.sql.twitch.streams.group.GroupRepository;
import com.expiredminotaur.bcukbot.sql.twitch.streams.streamer.Streamer;
import com.expiredminotaur.bcukbot.twitch.TwitchBot;
import com.expiredminotaur.bcukbot.twitch.command.chat.TwitchCommandEvent;
import com.github.twitch4j.helix.domain.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LiveStreamManager
{
    @Autowired
    private TwitchBot twitchBot;
    @Autowired
    private DiscordBot discordBot;
    @Autowired
    private GroupRepository groupRepository;

    Map<String, Map<String, StreamData>> streams = new HashMap<>();
    Map<String, MultiTwitchHandler> multiTwitchHandlers = new HashMap<>();

    @Scheduled(cron = "*/15 * * * * *")//every 15th second
    private void getStreams()
    {
        List<Group> groups = groupRepository.findAll();
        groups.forEach(group ->
        {
            Map<String, StreamData> groupData = streams.computeIfAbsent(group.getName(), k -> new HashMap<>());
            Set<Streamer> streamers = group.getStreamers();
            if (streamers.size() > 0)
            {
                List<String> streamerNames = streamers.stream().map(Streamer::getName).collect(Collectors.toList());
                List<Stream> streams = twitchBot.getStreams(streamerNames);
                streams.forEach(s ->
                {
                    StreamData streamData = groupData.computeIfAbsent(s.getUserName(), n -> new StreamData(twitchBot, discordBot));
                    streamData.update(group, s);
                });
            }
            groupData.entrySet().removeIf(s -> !s.getValue().checkValid(group));
            if (group.isMultiTwitch())
            {
                multiTwitchHandlers.computeIfAbsent(group.getName(), k -> new MultiTwitchHandler(discordBot)).update(groupData, group);
            }
        });
    }

    public Void getMultiTwitch(TwitchCommandEvent event)
    {
        for (MultiTwitchHandler mth : multiTwitchHandlers.values())
        {
            String link = mth.getMultiTwitch(event.getEvent().getChannel().getName());
            if (link != null)
                return event.respond(link);
        }
        return event.empty();
    }
}
