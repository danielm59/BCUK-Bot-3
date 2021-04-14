package com.expiredminotaur.bcukbot.web.security;

import com.expiredminotaur.bcukbot.web.view.MainView;
import com.expiredminotaur.bcukbot.web.view.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener
{
    private final UserTools userTools;

    @Autowired
    public ConfigureUIServiceInitListener(UserTools userTools)
    {
        this.userTools = userTools;
    }

    @Override
    public void serviceInit(ServiceInitEvent event)
    {
        event.getSource().addUIInitListener(uiEvent ->
        {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    private void beforeEnter(BeforeEnterEvent event)
    {
        final boolean accessGranted = SecurityUtils.isAccessGranted(event.getNavigationTarget(), userTools);
        if (!accessGranted)
        {
            if (SecurityUtils.isUserLoggedIn())
            {
                event.rerouteTo(MainView.class);
            } else
            {
                event.rerouteTo(LoginView.class);
            }
        }
    }
}
