package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.discord.DiscordBot;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "discord_bot", layout = MainLayout.class)
@AccessLevel(Role.MANAGER)
public class DiscordBotView extends BotView
{
    public DiscordBotView(@Autowired DiscordBot bot)
    {
        super(bot, "Discord");
    }
}
