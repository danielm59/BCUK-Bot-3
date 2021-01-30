package com.expiredminotaur.bcukbot.web.view.bot;

import com.expiredminotaur.bcukbot.BotService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class BotView extends VerticalLayout
{
    private final H2 header = new H2();
    private final BotService bot;
    private final String name;

    public BotView(BotService bot, String name)
    {
        this.bot = bot;
        this.name = name;
        setSizeFull();
        Button start = new Button("Start", e -> ButtonEvent(bot::start));
        Button stop = new Button("Stop", e -> ButtonEvent(bot::stop));
        Button restart = new Button("Restart", e -> ButtonEvent(bot::restart));
        updateHeader();
        add(header, start, stop, restart);
    }

    private void ButtonEvent(Runnable f)
    {
        f.run();
        updateHeader();
    }

    private void updateHeader()
    {
        if (bot.isRunning())
            header.setText(String.format("The %s Bot is Running", name));
        else
            header.setText(String.format("The %s Bot is Not Running", name));
    }
}
