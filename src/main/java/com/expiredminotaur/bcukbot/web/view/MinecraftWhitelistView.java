package com.expiredminotaur.bcukbot.web.view;

import com.expiredminotaur.bcukbot.sql.minecraft.Whitelist;
import com.expiredminotaur.bcukbot.sql.minecraft.WhitelistRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "minecraft", layout = MainLayout.class)
public class MinecraftWhitelistView extends VerticalLayout
{
    private final WhitelistRepository whitelist;
    private final Grid<Whitelist> grid;

    public MinecraftWhitelistView(@Autowired WhitelistRepository whitelist)
    {
        this.whitelist = whitelist;
        setSizeFull();
        grid = new Grid<>(Whitelist.class);
        grid.setColumns("discordID", "mcUUID", "mcName");
        grid.addColumn(new ComponentRenderer<>(whitelistEntry -> new Button("Delete", e -> delete(whitelistEntry))))
                .setHeader("Edit")
                .setFlexGrow(0);
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        add(grid);
    }

    private void delete(Whitelist whitelistEntry)
    {
        whitelist.delete(whitelistEntry);
        loadData();
    }

    @PostConstruct
    private void loadData()
    {
        grid.setItems(whitelist.findAll());
        grid.recalculateColumnWidths();
    }
}
