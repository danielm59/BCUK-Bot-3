package com.expiredminotaur.bcukbot.discord;

import com.expiredminotaur.bcukbot.discord.command.DiscordCommands;
import com.expiredminotaur.bcukbot.discord.music.SFXHandler;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class DiscordBot
{
    private Thread thread;
    private BotThread botThread;
    @Autowired
    DiscordCommands commands;
    @Autowired
    private SFXHandler sfxHandler;
    @Autowired
    private PointsSystem pointsSystem;

    public DiscordBot()
    {
        start();
    }

    public void start()
    {
        if (botThread == null)
        {
            botThread = new BotThread();
            thread = new Thread(botThread);
            thread.setName("DiscordBot");
            thread.start();
        }
    }

    public void stop()
    {
        if (botThread != null && botThread.gateway != null)
        {
            botThread.gateway.logout().block();
        }
        botThread = null;
        thread = null;
    }

    public void restart()
    {
        stop();
        start();
    }

    public boolean isRunning()
    {
        return botThread != null;
    }

    public DiscordClient getClient()
    {
        return botThread.client;
    }

    public GatewayDiscordClient getGateway()
    {
        return botThread.gateway;
    }

    public void sendMessage(Long channelID, String message)
    {
        botThread.gateway.getChannelById(Snowflake.of(channelID))
                .cast(MessageChannel.class).flatMap(c -> c.createMessage(message)).subscribe();
    }

    public Message sendAndGetMessage(Long channelID, String message)
    {
        return botThread.gateway.getChannelById(Snowflake.of(channelID))
                .cast(MessageChannel.class).flatMap(c -> c.createMessage(message)).block();
    }

    public Mono<Channel> getChannel(Long channelId)
    {
        return botThread.gateway.getChannelById(Snowflake.of(channelId));
    }

    public class BotThread implements Runnable
    {
        private DiscordClient client;
        private GatewayDiscordClient gateway;

        @Override
        public void run()
        {
            String token = System.getenv("BCUK_BOT_DISCORD_TOKEN");
            client = DiscordClient.create(token);
            gateway = client.login().block();

            gateway.on(MessageCreateEvent.class).subscribe(this::onMessage);

            gateway.onDisconnect().block();
        }

        private void onMessage(MessageCreateEvent event)
        {
            Optional<User> author = event.getMessage().getAuthor();
            if (author.isPresent() && !author.get().isBot() && event.getGuildId().isPresent())
            {
                String command = event.getMessage().getContent().split(" ", 2)[0];
                sfxHandler.play(command);
                commands.processCommand(event).subscribe();
                event.getMember().ifPresent(m -> pointsSystem.addXP(m));
            }
        }
    }
}
