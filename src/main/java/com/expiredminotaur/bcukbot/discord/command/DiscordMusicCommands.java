package com.expiredminotaur.bcukbot.discord.command;

import com.expiredminotaur.bcukbot.command.CommandEvent;
import com.expiredminotaur.bcukbot.discord.music.MusicHandler;
import com.expiredminotaur.bcukbot.discord.music.TrackData;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
public class DiscordMusicCommands
{
    @Autowired
    private MusicHandler musicHandler;

    public Mono<Void> join(DiscordCommandEvent event)
    {
        return Mono.justOrEmpty(event.getEvent().getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(channel -> musicHandler.joinChannel(channel));
    }

    public Mono<Void> play(DiscordCommandEvent event)
    {
        return play("", event);
    }

    public Mono<Void> playYT(DiscordCommandEvent event)
    {
        return play("ytsearch:", event);
    }

    private Mono<Void> play(String prefix, DiscordCommandEvent event)
    {
        return Mono.justOrEmpty(event.getFinalMessage())
                .map(content -> Arrays.asList(content.split(" ", 2)))
                .filter(command -> command.size() > 1)
                .doOnNext(command -> musicHandler.loadAndPlay(event.getEvent(), prefix + command.get(1)))
                .then();
    }

    public Mono<Void> skip(DiscordCommandEvent event)
    {
        musicHandler.getScheduler().nextTrack();
        return event.getEvent().getMessage().getChannel().flatMap(mc -> mc.createMessage("Skipped to next track.")).then();
    }

    public Mono<Void> stop(DiscordCommandEvent event)
    {
        musicHandler.getScheduler().clear();
        musicHandler.getScheduler().nextTrack();
        return event.getEvent().getMessage().getChannel().flatMap(mc -> mc.createMessage("Music stopped and queue cleared")).then();
    }

    public Mono<Void> list(DiscordCommandEvent event)
    {
        String message = event.getFinalMessage();
        if (!message.isEmpty())
        {
            String[] messageArray = message.split(" ");

            if (messageArray.length > 1 && NumberUtils.isCreatable(messageArray[1]))
            {
                return musicHandler.listTracks(event.getEvent(), NumberUtils.createInteger(messageArray[1]));
            }
        }
        return musicHandler.listTracks(event.getEvent(), 1);
    }

    public Mono<Void> playing(DiscordCommandEvent event)
    {

        AudioTrack track = musicHandler.getScheduler().currentTrack();
        if (track != null)
        {
            String playing = "Playing: " + track.getInfo().title;
            if (track.getUserData(TrackData.class).getRequestedBy() != null)
            {
                playing += " Requested by: " + track.getUserData(TrackData.class).getRequestedBy();
            }
            String finalPlaying = playing;
            return event.getEvent().getMessage().getChannel().flatMap(mc -> mc.createMessage(finalPlaying)).then();
        } else
        {
            return event.getEvent().getMessage().getChannel().flatMap(mc -> mc.createMessage("No Track is currently Playing")).then();
        }
    }

    public Mono<Void> volume(CommandEvent<?> event)
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
        return Mono.empty();
    }

    public Mono<Void> pause(CommandEvent<?> event)
    {
        return musicHandler.togglePause(event);
    }

    public Mono<Void> leave(DiscordCommandEvent event)
    {
        Mono<MessageChannel> channel = event.getEvent().getMessage().getChannel();
        if (musicHandler.leaveChannel())
            return channel.flatMap(mc -> mc.createMessage("Music stopped and queue cleared"))
                    .then(channel.flatMap(mc -> mc.createMessage("Goodbye")).then());
        else
            return channel.flatMap(mc -> mc.createMessage("I can't leave a room if i'm not in one")).then();
    }
}
