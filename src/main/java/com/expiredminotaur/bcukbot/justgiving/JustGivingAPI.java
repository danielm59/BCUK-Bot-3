package com.expiredminotaur.bcukbot.justgiving;

import com.expiredminotaur.bcukbot.command.CommandEvent;
import com.expiredminotaur.bcukbot.twitch.TwitchBot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class JustGivingAPI
{
    private final Logger log = LoggerFactory.getLogger(JustGivingAPI.class);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final TwitchBot bot;
    private final JustGivingSettings settings;
    private String data;
    private ScheduledFuture<?> task = null;

    public JustGivingAPI(@Autowired TwitchBot bot)
    {
        this.bot = bot;
        JustGivingSettings settings;
        try (FileReader fr = new FileReader(JustGivingSettings.fileName))
        {
            settings = gson.fromJson(fr, JustGivingSettings.class);
        } catch (IOException e)
        {
            log.warn("Can't load just giving settings file, this may be because one has not been made yet");
            settings = new JustGivingSettings();
        }
        this.settings = settings;
        saveSettings();
    }

    public void saveSettings()
    {
        try (FileWriter fw = new FileWriter(JustGivingSettings.fileName))
        {
            gson.toJson(settings, fw);
        } catch (IOException e)
        {
            log.error("Unable to save just giving setting file", e);
        }
        updateScheduler();
    }

    private void updateScheduler()
    {
        if (settings.autoCheckEnabled && task == null)
        {
            task = scheduler.scheduleAtFixedRate(this::checkForNewData, 0, 1, TimeUnit.SECONDS);
        } else if (!settings.autoCheckEnabled && task != null)
        {
            task.cancel(false);
            task = null;
        }
    }

    private BufferedReader request(URL url) throws Exception
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        switch (conn.getResponseCode())
        {
            case 200:
                return new BufferedReader(new InputStreamReader((conn.getInputStream())));
            case 304:
                //no change
                return null;
            default:
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + " From:" + url.toString());
        }
    }

    private void checkForNewData()
    {
        try
        {
            URL url = new URL(String.format("https://api.justgiving.com/%s/v1/fundraising/pages/%s", settings.appId, settings.campaignName));
            BufferedReader br = request(url);
            String output;
            if (br != null && (output = br.readLine()) != null)
            {
                updateData(output);
            }
        } catch (Exception e)
        {
            log.error("Justgiving API error", e);
            try
            {
                //If the last check failed wait for 30 seconds before trying again
                Thread.sleep(30000);
            } catch (InterruptedException e2)
            {
                log.error("Justgiving Sleep Interrupted Exception", e2);
            }
        }
    }

    private void updateData(String data)
    {
        this.data = data;
        JsonElement jsonTree = JsonParser.parseString(data);
        if (jsonTree.isJsonObject())
        {
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            String total = jsonObject.get("grandTotalRaisedExcludingGiftAid").getAsString();
            if (!total.equalsIgnoreCase(settings.lastTotal))
            {
                settings.lastTotal = total;
                saveSettings();
                sendMessageToAll();
            }
        }
    }

    private void sendMessageToAll()
    {
        String message = getTotalRaisedMessage();
        if (message != null && settings.channels != null)
        {
            for (String channel : settings.channels)
                bot.sendMessage(channel, message);
        }
    }

    public <R> R amountRaised(CommandEvent<?, R> event)
    {
        String message = getTotalRaisedMessage();
        if (message != null)
        {
            return event.respond(message);
        }
        return event.empty();
    }

    private String getTotalRaisedMessage()
    {
        JsonElement jsonTree = JsonParser.parseString(data);
        if (jsonTree.isJsonObject())
        {
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            String total = jsonObject.get("grandTotalRaisedExcludingGiftAid").getAsString();
            String target = jsonObject.get("fundraisingTarget").getAsString();
            String percentage = jsonObject.get("totalRaisedPercentageOfFundraisingTarget").getAsString();
            String message = settings.message;
            message = message.replace("$total", total);
            message = message.replace("$target", target);
            message = message.replace("$percentage", percentage);
            return message;
        }
        return null;
    }

    public JustGivingSettings getSettings()
    {
        return settings;
    }
}