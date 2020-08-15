package com.expiredminotaur.bcukbot.discord.music;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import java.nio.ByteBuffer;

public class AudioProvider extends discord4j.voice.AudioProvider
{
    private final AudioPlayer audioPlayer;
    private final MutableAudioFrame frame = new MutableAudioFrame();

    AudioProvider(final AudioPlayer audioPlayer)
    {
        super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
        frame.setBuffer(getBuffer());
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean provide()
    {
        final boolean didProvide = audioPlayer.provide(frame);
        if (didProvide)
        {
            getBuffer().flip();
        }
        return didProvide;
    }
}
