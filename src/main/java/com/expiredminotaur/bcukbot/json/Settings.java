package com.expiredminotaur.bcukbot.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class Settings
{
    private final Logger log = LoggerFactory.getLogger(Settings.class);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private SettingsJson settings;

    public Settings()
    {
        try (FileReader fr = new FileReader(SettingsJson.getFileName()))
        {
            settings = gson.fromJson(fr, SettingsJson.class);
        } catch (IOException e)
        {
            log.warn("Can't load settings file, this may be because one has not been made yet");
            settings = new SettingsJson();
        }
        saveSettings();
    }

    public int getMusicVolume()
    {
        return settings.musicVolume;
    }

    public void setMusicVolume(int musicVolume)
    {
        settings.musicVolume = musicVolume;
        saveSettings();
    }

    public int getSfxVolume()
    {
        return settings.sfxVolume;
    }

    public void setSfxVolume(int sfxVolume)
    {
        settings.sfxVolume = sfxVolume;
        saveSettings();
    }

    public int getSfxDelay()
    {
        return settings.sfxDelay;
    }

    public void setSfxDelay(int delay)
    {
        settings.sfxDelay = delay;
        saveSettings();
    }

    public long getSongAnnouncementChannel()
    {
        return settings.songAnnouncementChannel;
    }

    public void setSongAnnouncementChannel(long songAnnouncementChannel)
    {
        settings.songAnnouncementChannel = songAnnouncementChannel;
        saveSettings();
    }

    private void saveSettings()
    {
        try (FileWriter fw = new FileWriter(SettingsJson.getFileName()))
        {
            gson.toJson(settings, fw);
        } catch (IOException e)
        {
            log.error("Unable to save setting file", e);
        }
    }
}


