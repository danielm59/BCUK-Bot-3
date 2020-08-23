package com.expiredminotaur.bcukbot.web.view.login;

import com.expiredminotaur.bcukbot.web.security.UserTools;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("unauthorised")
public class UnauthorisedView extends VerticalLayout
{
    public UnauthorisedView(@Autowired UserTools userTools)
    {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        H1 message1 = new H1("You are not authorised to access this, please contact the owner");
        H1 message2 = new H1("Reference: " + userTools.getCurrentUsersID());
        add(message1, message2);
    }
}
