package com.expiredminotaur.bcukbot.justgiving;

import javax.persistence.Transient;
import java.util.Set;

public class JustGivingSettings
{
    @Transient
    static String fileName = "justgiving.json";
    Boolean autoCheckEnabled = false;
    Set<String> channels;
    String appId;
    String campaignName;
    String lastTotal;
    String message;

    public static String getFileName()
    {
        return fileName;
    }

    public static void setFileName(String fileName)
    {
        JustGivingSettings.fileName = fileName;
    }

    public Boolean getAutoCheckEnabled()
    {
        return autoCheckEnabled;
    }

    public void setAutoCheckEnabled(Boolean autoCheckEnabled)
    {
        this.autoCheckEnabled = autoCheckEnabled;
    }

    public Set<String> getChannels()
    {
        return channels;
    }

    public void setChannels(Set<String> channels)
    {
        this.channels = channels;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getCampaignName()
    {
        return campaignName;
    }

    public void setCampaignName(String campaignName)
    {
        this.campaignName = campaignName;
    }

    public String getLastTotal()
    {
        return lastTotal;
    }

    public void setLastTotal(String lastTotal)
    {
        this.lastTotal = lastTotal;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
