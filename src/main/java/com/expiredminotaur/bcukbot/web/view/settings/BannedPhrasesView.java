package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.sql.twitch.bannedphrase.BannedPhrase;
import com.expiredminotaur.bcukbot.sql.twitch.bannedphrase.BannedPhraseRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@Route(layout = MainLayout.class, value = "settings/banned_phrases")
@Secured({"MOD", "ADMIN"})
public class BannedPhrasesView extends HorizontalLayout
{
    private final BannedPhraseRepository data;
    private final Grid<BannedPhrase> grid = new Grid<>(BannedPhrase.class);
    private final TextField newPhrase = new TextField("New Phrase");

    public BannedPhrasesView(@Autowired BannedPhraseRepository data)
    {
        this.data = data;
        setSizeFull();

        VerticalLayout leftLayout = new VerticalLayout();
        Button add = new Button("Add", e -> add());
        leftLayout.add(newPhrase, add);

        VerticalLayout rightLayout = new VerticalLayout();
        grid.setColumns("phrase");
        grid.addColumn(new ComponentRenderer<>(phrase -> new Button("Delete", e -> delete(phrase))))
                .setHeader("Delete")
                .setFlexGrow(0);
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.recalculateColumnWidths();
        grid.setItems(data.findAll());
        rightLayout.add(grid);

        add(leftLayout, rightLayout);
        setFlexGrow(0, leftLayout);
        setFlexGrow(1, grid);
    }

    private void delete(BannedPhrase phrase)
    {
        data.delete(phrase);
        grid.setItems(data.findAll());
    }

    private void add()
    {
        if (!newPhrase.getValue().isEmpty())
        {
            BannedPhrase newBannedPhrase = new BannedPhrase();
            newBannedPhrase.setPhrase(newPhrase.getValue());
            data.save(newBannedPhrase);
            newPhrase.setValue("");
            grid.setItems(data.findAll());
        }
    }
}
