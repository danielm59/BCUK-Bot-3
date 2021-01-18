package com.expiredminotaur.bcukbot.web.view;

import com.expiredminotaur.bcukbot.sql.minecraft.Whitelist;
import com.expiredminotaur.bcukbot.sql.minecraft.WhitelistRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "minecraft", layout = MainLayout.class)
public class MinecraftWhitelistView extends VerticalLayout
{
    @Autowired
    WhitelistRepository whitelist;

    Grid<Whitelist> grid;

    public MinecraftWhitelistView()
    {
        setSizeFull();
        grid = new Grid<>(Whitelist.class);
        grid.setColumns("discordID", "mcUUID", "mcName");
        add(grid);
    }

    @PostConstruct
    private void loadData()
    {
        grid.setItems(whitelist.findAll());
    }
}
