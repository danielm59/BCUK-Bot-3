package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.discord.DiscordBot;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "discord/bot", layout = MainLayout.class)
public class DiscordBotView extends BotView
{
    public DiscordBotView(@Autowired DiscordBot bot)
    {
        super(bot, "Discord");
    }
}
