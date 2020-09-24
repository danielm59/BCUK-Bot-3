package com.expiredminotaur.bcukbot.web.view.collection;

import com.expiredminotaur.bcukbot.sql.collection.joke.Joke;
import com.expiredminotaur.bcukbot.sql.collection.joke.JokeRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.UserTools;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "jokes", layout = MainLayout.class)
public class JokeView extends CollectionView<Joke>
{
    public JokeView(@Autowired UserTools userTools, @Autowired JokeRepository repository)
    {
        super(userTools, repository, new Grid<>(Joke.class), new Binder<>(Joke.class));
        setup("Jokes", "joke", "Joke");
    }
}