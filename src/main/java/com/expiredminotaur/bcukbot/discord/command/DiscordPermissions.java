package com.expiredminotaur.bcukbot.discord.command;

import discord4j.common.util.Snowflake;
import discord4j.rest.util.Permission;

public class DiscordPermissions
{
    public static boolean admin(DiscordCommandEvent event)
    {
        return event.getEvent().getMember().map(m -> m.getBasePermissions().map(ps -> ps.contains(Permission.MANAGE_MESSAGES)).block()).orElse(false);
    }

    public static boolean general(DiscordCommandEvent event)
    {
        return true;
    }

    public static boolean hasRole(DiscordCommandEvent event, Snowflake roleID)
    {
        return event.getEvent().getMember().map(value -> value.getRoleIds().contains(roleID)).orElse(false);
    }
}
