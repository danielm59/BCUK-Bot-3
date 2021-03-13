package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.json.Settings;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.data.validator.LongRangeValidator;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@Route(value = "settings/music", layout = MainLayout.class)
@Secured("ADMIN")
public class MusicSettingsView extends VerticalLayout
{
    public MusicSettingsView(@Autowired Settings settings)
    {
        Binder<Settings> settingsBinder = new Binder<>();

        TextField musicVolume = new TextField("Music Volume");
        TextField sfxVolume = new TextField("SFX Volume");
        TextField sfxDelay = new TextField("Delay between SFX commands (second)");
        TextField songAnnouncementChannel = new TextField("Song Announcement Channel");


        settingsBinder.forField(musicVolume)
                .withConverter(new StringToIntegerConverter("Must be a number"))
                .withValidator(new IntegerRangeValidator("Must be between 0 and 100", 0, 100))
                .bind(Settings::getMusicVolume, Settings::setMusicVolume);

        settingsBinder.forField(sfxVolume)
                .withConverter(new StringToIntegerConverter("Must be a number"))
                .withValidator(new IntegerRangeValidator("Must be between 0 and 100", 0, 100))
                .bind(Settings::getSfxVolume, Settings::setSfxVolume);

        settingsBinder.forField(sfxDelay)
                .withConverter(new StringToLongConverter("Must be a number"))
                .withValidator(new LongRangeValidator("Must be 0 or greater", 0L, Long.MAX_VALUE))
                .bind(Settings::getSfxDelay, Settings::setSfxDelay);

        settingsBinder.forField(songAnnouncementChannel)
                .withConverter(new StringToLongConverter("Must be a number"))
                .withValidator(new LongRangeValidator("Must be -1 or greater", -1L, Long.MAX_VALUE))
                .bind(Settings::getSongAnnouncementChannel, Settings::setSongAnnouncementChannel);

        settingsBinder.readBean(settings);

        Button save = new Button("Save", e ->
        {
            try
            {
                settingsBinder.writeBean(settings);
                Notification.show("Settings Saved");
            } catch (ValidationException ex)
            {
                Notification.show("Error Saving Settings");
            }
        });

        add(musicVolume, sfxVolume, sfxDelay, songAnnouncementChannel, save);
    }
}
