package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.twitch.TwitchBot;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "twitch/bot", layout = MainLayout.class)
public class TwitchBotView extends BotView
{
    public TwitchBotView(@Autowired TwitchBot bot)
    {
        super(bot, "Twitch");
    }
}
