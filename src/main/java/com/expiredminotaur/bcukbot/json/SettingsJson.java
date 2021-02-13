package com.expiredminotaur.bcukbot.json;

public class SettingsJson
{
    protected int musicVolume = 30;
    protected int sfxVolume = 100;
    protected long sfxDelay = 15;
    protected long songAnnouncementChannel = -1L;

    static String getFileName()
    {
        return "settings.json";
    }
}