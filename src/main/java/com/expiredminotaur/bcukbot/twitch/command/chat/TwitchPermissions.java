package com.expiredminotaur.bcukbot.twitch.command.chat;

import com.github.twitch4j.common.enums.CommandPermission;

public class TwitchPermissions
{

    public static boolean everyone(TwitchCommandEvent event)
    {
        return true;
    }

    static boolean sub(TwitchCommandEvent event)
    {
        return event.getEvent().getPermissions().contains(CommandPermission.SUBSCRIBER);
    }

    static boolean vip(TwitchCommandEvent event)
    {
        return event.getEvent().getPermissions().contains(CommandPermission.VIP);
    }

    public static boolean mod(TwitchCommandEvent event)
    {
        return event.getEvent().getPermissions().contains(CommandPermission.MODERATOR);
    }

    public static boolean broadcaster(TwitchCommandEvent event)
    {
        return event.getEvent().getPermissions().contains(CommandPermission.BROADCASTER);
    }

    public static boolean subPlus(TwitchCommandEvent event)
    {
        return sub(event) || vipPlus(event);
    }

    public static boolean vipPlus(TwitchCommandEvent event)
    {
        return vip(event) || modPlus(event);
    }

    public static boolean modPlus(TwitchCommandEvent event)
    {
        return mod(event) || broadcaster(event);
    }
}
