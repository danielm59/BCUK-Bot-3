package com.expiredminotaur.bcukbot.twitch.command;

import com.github.twitch4j.common.enums.CommandPermission;

public class TwitchPermissions
{

    static boolean everyone(TwitchCommandEvent event)
    {
        return true;
    }

    static boolean vip(TwitchCommandEvent event)
    {
        return event.getEvent().getPermissions().contains(CommandPermission.VIP);
    }

    static boolean mod(TwitchCommandEvent event)
    {
        return event.getEvent().getPermissions().contains(CommandPermission.MODERATOR);
    }

    static boolean broadcaster(TwitchCommandEvent event)
    {
        return event.getEvent().getPermissions().contains(CommandPermission.BROADCASTER);
    }

    static boolean vipPlus(TwitchCommandEvent event)
    {
        return vip(event) || modPlus(event);
    }

    static boolean modPlus(TwitchCommandEvent event)
    {
        return mod(event) || broadcaster(event);
    }
}
