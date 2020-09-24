package com.expiredminotaur.bcukbot.web.view.collection;

import com.expiredminotaur.bcukbot.sql.collection.quote.Quote;
import com.expiredminotaur.bcukbot.sql.collection.quote.QuoteRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.UserTools;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "quotes", layout = MainLayout.class)
public class QuoteView extends CollectionView<Quote>
{
    public QuoteView(@Autowired UserTools userTools, @Autowired QuoteRepository repository)
    {
        super(userTools, repository, new Grid<>(Quote.class), new Binder<>(Quote.class));
        setup("Quotes", "quote", "Quote");
    }
}