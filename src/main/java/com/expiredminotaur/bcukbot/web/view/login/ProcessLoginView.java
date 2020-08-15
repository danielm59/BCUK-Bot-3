package com.expiredminotaur.bcukbot.web.view.login;

import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.web.security.UserTools;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login/process")
public class ProcessLoginView extends Div implements AfterNavigationObserver
{
    @Autowired
    private UserRepository users;

    @Autowired
    private UserTools userTools;

    @Override
    public void afterNavigation(AfterNavigationEvent event)
    {
        Long userID = userTools.getCurrentUsersID();
        String username = userTools.getCurrentUsersName();
        User user = users.findById(userID).orElse(new User(userID));
        user.setDiscordName(username);
        users.save(user);
        UI.getCurrent().getPage().setLocation("/");
    }
}
