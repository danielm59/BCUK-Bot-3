package com.expiredminotaur.bcukbot.web.view.admin;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import static com.expiredminotaur.bcukbot.web.view.stream.StreamView.Service.broadcast;

@AccessLevel(Role.ADMIN)
@Route(value = "/stream_broadcaster", layout = MainLayout.class)
public class StreamBroadcasterView extends VerticalLayout
{
    public StreamBroadcasterView()
    {
        TextField input = new TextField();
        Button button = new Button("Send Message", e -> broadcast(input.getValue()));
        add(input, button);
    }
}
