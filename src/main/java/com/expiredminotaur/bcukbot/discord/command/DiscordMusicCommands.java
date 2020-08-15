package com.expiredminotaur.bcukbot.discord.command;

import com.expiredminotaur.bcukbot.discord.music.MusicHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.voice.VoiceConnection;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
public class DiscordMusicCommands
{
    private VoiceConnection voiceConnection;

    @Autowired
    private MusicHandler musicHandler;

    public Mono<Void> join(DiscordCommandEvent event)
    {
        if (voiceConnection != null)
        {
            try
            {
                voiceConnection.disconnect();
            } finally
            {
                voiceConnection = null;
            }
        }
        Mono.justOrEmpty(event.getEvent().getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(channel -> channel.join(spec -> spec.setProvider(musicHandler.getProvider())))
                .subscribe(vc -> voiceConnection = vc);
        return Mono.empty();
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
            return event.getEvent().getMessage().getChannel().flatMap(mc -> mc.createMessage("Playing: " + track.getInfo().title)).then();
        } else
        {
            return event.getEvent().getMessage().getChannel().flatMap(mc -> mc.createMessage("No Track is currently Playing")).then();
        }
    }

    public Mono<Void> volume(DiscordCommandEvent event)
    {
        String content = event.getFinalMessage();

        if (!content.isEmpty())
        {
            String[] args = content.split(" ");
            if (args.length > 1)
            {
                return musicHandler.setVolume(event.getEvent(), args[1]);
            } else
            {
                return musicHandler.getVolume(event.getEvent());
            }
        }
        return Mono.empty();
    }

    public Mono<Void> pause(DiscordCommandEvent event)
    {
        return musicHandler.togglePause(event.getEvent());
    }

    public Mono<Void> leave(DiscordCommandEvent event)
    {
        Mono<MessageChannel> channel = event.getEvent().getMessage().getChannel();
        if (voiceConnection != null)
        {
            try
            {
                musicHandler.getScheduler().clear();
                musicHandler.getScheduler().nextTrack();
                voiceConnection.disconnect().subscribe();
            } finally
            {
                voiceConnection = null;
            }
            return channel.flatMap(mc -> mc.createMessage("Music stopped and queue cleared"))
                    .then(channel.flatMap(mc -> mc.createMessage("Goodbye")).then());
        }
        return channel.flatMap(mc -> mc.createMessage("I can't leave a room if i'm not in one")).then();
    }
}
