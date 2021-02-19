package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.justgiving.JustGivingAPI;
import com.expiredminotaur.bcukbot.justgiving.JustGivingSettings;
import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

@Route(value = "settings/justgiving", layout = MainLayout.class)
public class JustGivingView extends VerticalLayout
{
    private final Logger logger = LoggerFactory.getLogger(JustGivingView.class);

    public JustGivingView(@Autowired JustGivingAPI justGivingAPI, @Autowired UserRepository users)
    {
        Binder<JustGivingSettings> binder = new Binder<>(JustGivingSettings.class);

        Checkbox enabled = new Checkbox("Enabled");
        MultiselectComboBox<String> channels = new MultiselectComboBox<>("Twitch Channels");
        TextField appId = new TextField("App ID");
        TextField campaignName = new TextField("Campaign Name");
        campaignName.setWidthFull();
        TextField message = new TextField("Message");
        message.setWidthFull();

        channels.setItems(users.chatBotUsers().stream().map(User::getTwitchName));

        binder.bind(enabled, "autoCheckEnabled");
        binder.bind(channels, "channels");
        binder.bind(appId, "appId");
        binder.bind(campaignName, "campaignName");
        binder.bind(message, "message");

        Button save = new Button("Save", e ->
        {
            try
            {
                binder.writeBean(justGivingAPI.getSettings());
                justGivingAPI.saveSettings();
            } catch (ValidationException validationException)
            {
                logger.error("Justgiving settings error", validationException);
            }
        });

        Label test = new Label("Clear total raised to force twitch message and sfx");
        Button testButton = new Button("Test", e ->
        {
            justGivingAPI.getSettings().setLastTotal("");
            justGivingAPI.saveSettings();
        });

        add(enabled, channels, appId, campaignName, message, save, test, testButton);
        binder.readBean(justGivingAPI.getSettings());
    }
}
