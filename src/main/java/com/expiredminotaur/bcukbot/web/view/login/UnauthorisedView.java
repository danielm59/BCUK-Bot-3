package com.expiredminotaur.bcukbot.web.view.login;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("unauthorised")
public class UnauthorisedView extends VerticalLayout
{
    public UnauthorisedView()
    {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        H1 message1 = new H1("You must be in a server with the bot to access this");
        add(message1);
    }
}
