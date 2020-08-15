package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.discord.DiscordBot;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "discord/bot", layout = MainLayout.class)
public class DiscordBotView extends VerticalLayout
{
    public DiscordBotView(@Autowired DiscordBot bot)
    {
        setSizeFull();
        Button stop = new Button("Stop", e -> bot.stop());
        Button start = new Button("Start", e -> bot.start());
        Button restart = new Button("Restart", e -> bot.restart());
        add(stop, start, restart);
    }
}
