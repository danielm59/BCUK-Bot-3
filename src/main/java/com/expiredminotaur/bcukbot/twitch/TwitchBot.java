package com.expiredminotaur.bcukbot.twitch;

import com.expiredminotaur.bcukbot.discord.music.SFXHandler;
import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.twitch.command.chat.TwitchCommandEvent;
import com.expiredminotaur.bcukbot.twitch.command.chat.TwitchCommands;
import com.expiredminotaur.bcukbot.twitch.command.whisper.WhisperCommands;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.user.PrivateMessageEvent;
import com.github.twitch4j.helix.domain.Game;
import com.github.twitch4j.helix.domain.Stream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Component
public class TwitchBot
{
    private final Logger log = LoggerFactory.getLogger(TwitchBot.class);
    @Autowired
    private TwitchCommands twitchCommands;
    @Autowired
    private WhisperCommands whisperCommands;
    @Autowired
    private SFXHandler sfxHandler;
    @Autowired
    private BanHandler banHandler;
    private final UserRepository userRepository;
    private TwitchClient twitchClient;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private String accessToken;

    public TwitchBot(@Autowired UserRepository userRepository)
    {
        this.userRepository = userRepository;
        start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private static String getAccessToken() throws Exception
    {
        String url = "https://id.twitch.tv/oauth2/token?" +
                "client_id=" + URLEncoder.encode(System.getenv("BCUK_BOT_TWITCH_CLIENT_ID"), "UTF-8") +
                "&client_secret=" + URLEncoder.encode(System.getenv("BCUK_BOT_TWITCH_CLIENT_SECRET"), "UTF-8") +
                "&grant_type=client_credentials";

        HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
        conn.setRequestMethod("POST");
        conn.connect();

        if (conn.getResponseCode() != 200)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + " Getting access token");
        }
        JsonElement json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream()));
        if (json.isJsonObject())
        {
            return ((JsonObject) json).get("access_token").getAsString();
        } else
            throw new RuntimeException(("Error reading access token"));
    }

    public void restart()
    {
        stop();
        start();
    }

    public void start()
    {
        try
        {
            accessToken = getAccessToken();
        } catch (Exception e)
        {
            log.error("Failed to get access Token", e);
            return;
        }
        setupThreads();
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();
        OAuth2Credential chatOAuth = new OAuth2Credential("twitch", System.getenv("BCUK_BOT_TWITCH_CHAT_OAUTH"));
        OAuth2Credential appOAuth = new OAuth2Credential("twitch", accessToken);
        twitchClient = clientBuilder
                .withClientId(System.getenv("BCUK_BOT_TWITCH_CLIENT_ID"))
                .withClientSecret(System.getenv("BCUK_BOT_TWITCH_CLIENT_SECRET"))
                .withEnableHelix(true)
                .withChatAccount(chatOAuth)
                .withEnableChat(true)
                .withScheduledThreadPoolExecutor(scheduledThreadPoolExecutor)
                .withDefaultAuthToken(appOAuth)
                .build();
        setupEvents(accessToken);
        joinChannels();
    }

    public void stop()
    {
        twitchClient.close();
        scheduledThreadPoolExecutor.shutdown();
        scheduledThreadPoolExecutor = null;
    }

    private void setupThreads()
    {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("twitch_chat-%d")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .build();

        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
        scheduledThreadPoolExecutor.setThreadFactory(threadFactory);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        scheduledThreadPoolExecutor.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() * 8);
    }

    private void setupEvents(String accessToken)
    {
        SimpleEventHandler handler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);
        handler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
        handler.onEvent(PrivateMessageEvent.class, this::onWhisper);
    }

    private void onChannelMessage(ChannelMessageEvent event)
    {
        TwitchCommandEvent cEvent = new TwitchCommandEvent(event);
        if(!banHandler.checkBannedPhrases(cEvent))
        {
            String command = event.getMessage().split(" ", 2)[0];
            sfxHandler.play(command);
            twitchCommands.processCommand(cEvent);
        }
    }

    private void onWhisper(PrivateMessageEvent event)
    {
        whisperCommands.processCommand(event, twitchClient);
    }

    public void joinChannels()
    {
        for (User user : userRepository.chatBotUsers())
        {
            String name = user.getTwitchName();
            if (name != null && !name.equals(""))
            {
                twitchClient.getChat().joinChannel(name);
            }
        }

    }

    public void sendMessage(String channel, String message)
    {
        twitchClient.getChat().sendMessage(channel, message);
    }

    public List<Stream> getStreams(List<String> channels)
    {
        return twitchClient.getHelix().getStreams(accessToken, "", null, 1, null, null, null, channels).execute().getStreams();
    }

    //TODO: cache game names
    public String getGameName(String gameId)
    {
        if (gameId != null)
        {
            List<Game> games = twitchClient.getHelix().getGames(accessToken, Collections.singletonList(gameId), null).execute().getGames();
            if (games.size() == 1)
            {
                return games.get(0).getName();
            }
        }
        return "?Unknown Game?";
    }
}
