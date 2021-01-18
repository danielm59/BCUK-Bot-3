package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.twitch.TwitchBot;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "twitch/bot", layout = MainLayout.class)
public class TwitchBotView extends VerticalLayout
{
    public TwitchBotView(@Autowired TwitchBot bot)
    {
        Button start = new Button("Start", e -> bot.start());
        Button stop = new Button("Stop", e -> bot.stop());
        Button restart = new Button("Restart", e -> bot.restart());
        add(start, stop, restart);
    }
}
