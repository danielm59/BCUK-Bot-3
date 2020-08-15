package com.expiredminotaur.bcukbot.discord.music;

import com.expiredminotaur.bcukbot.discord.DiscordBot;
import com.expiredminotaur.bcukbot.json.Settings;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends AudioEventAdapter
{
    private final DiscordBot discordBot;
    private final AudioPlayer player;
    private final LinkedBlockingDeque<AudioTrack> queue;
    private final Settings settings;
    private boolean sfx = false;
    private boolean resume = false;

    TrackScheduler(final AudioPlayer player, DiscordBot discordBot, Settings settings)
    {
        this.discordBot = discordBot;
        this.player = player;
        this.settings = settings;
        player.addListener(this);
        this.queue = new LinkedBlockingDeque<>();
    }

    void queue(AudioTrack track)
    {
        if (!player.startTrack(track, true))
        {
            queue.offer(track);
        }
    }

    void playPriority(AudioTrack track)
    {
        player.setPaused(true);
        if (player.getPlayingTrack() != null)
        {
            AudioTrack clone = player.getPlayingTrack().makeClone();
            clone.setPosition(player.getPlayingTrack().getPosition());
            queue.offerFirst(clone);
            resume = true;
        }
        queue.offerFirst(track);
        player.setVolume(settings.getSfxVolume());
        sfx = true;
        nextTrack();
        player.setPaused(false);
    }

    public void clear()
    {
        queue.clear();
    }

    public AudioTrack currentTrack()
    {
        return player.getPlayingTrack();
    }

    public void nextTrack()
    {
        discordBot.getGateway().updatePresence(Presence.online()).subscribe();
        if (!sfx)
        {
            player.setVolume(settings.getMusicVolume());
        }
        player.startTrack(queue.poll(), false);
    }

    public BlockingQueue<AudioTrack> getPlaylist()
    {
        return queue;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track)
    {
        if (!sfx)
        {
            if (!resume)
            {
                discordBot.getGateway().updatePresence(Presence.online(Activity.listening(currentTrack().getInfo().title))).subscribe();

                String playing = "Playing: " + track.getInfo().title;
                if (track.getUserData(String.class) != null)
                {
                    playing += " Requested by: " + track.getUserData(String.class);
                }

                long channelId = settings.getSongAnnouncementChannel();
                if (channelId >= 0)
                    discordBot.sendMessage(channelId, playing);
                //TODO send song to live twitch channels
                /*
                for (Map.Entry<String, Long> twitchChat : Config.getInstance().getTwitchChats().entrySet())
                {
                    if (LiveStreams.checkLive(twitchChat.getKey()))
                    {
                        IRCBot.getInstance().sendMessage("#" + twitchChat.getKey(), playing);
                    }
                }
                 */
            } else
            {
                resume = false;
            }
        } else
        {
            sfx = false;
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        discordBot.getGateway().updatePresence(Presence.online()).subscribe();
        if (endReason.mayStartNext)
        {
            nextTrack();
        }
    }
}
