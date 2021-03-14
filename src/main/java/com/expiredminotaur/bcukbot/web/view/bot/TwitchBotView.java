package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.twitch.TwitchBot;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "twitch/bot", layout = MainLayout.class)
@AccessLevel(Role.MOD)
public class TwitchBotView extends BotView
{
    public TwitchBotView(@Autowired TwitchBot bot)
    {
        super(bot, "Twitch");
    }
}
