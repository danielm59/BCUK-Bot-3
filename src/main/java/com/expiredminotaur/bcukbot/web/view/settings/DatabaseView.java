package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "settings/database", layout = MainLayout.class)
public class DatabaseView extends VerticalLayout
{
    //TODO add a way to register a user (enter discordID/reference)
    //TODO add a way to invalidate all caches
}
