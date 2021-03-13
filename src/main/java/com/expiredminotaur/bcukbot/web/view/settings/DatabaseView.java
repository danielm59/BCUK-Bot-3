package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.annotation.Secured;

import java.util.Objects;

@Route(value = "settings/database", layout = MainLayout.class)
@Secured("ADMIN")
public class DatabaseView extends VerticalLayout
{
    public DatabaseView(@Autowired UserRepository users, @Autowired CacheManager cacheManager)
    {
        Button registerUserButton = new Button("Register User", e -> registerUser(users));
        Button resetCacheButton = new Button("Reset Cache", e -> resetCache(cacheManager));
        add(registerUserButton, resetCacheButton);
    }

    private void registerUser(UserRepository users)
    {
        Dialog dialog = new Dialog();
        FormLayout layout = new FormLayout();
        Binder<User> binder = new Binder<>(User.class);

        TextField discordID = new TextField();
        binder.forField(discordID).withConverter(new StringToLongConverter("Invalid Discord ID"))
                .withValidator(Objects::nonNull, "Required")
                .withValidator(v -> !users.findById(v).isPresent(), "User already registered")
                .bind("discordId");
        layout.addFormItem(discordID, "Discord ID/Reference");

        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e -> save(binder, users, dialog));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(save, cancel);

        dialog.add(layout, buttons);
        dialog.setCloseOnOutsideClick(false);
        dialog.open();
    }

    private void save(Binder<User> binder, UserRepository users, Dialog dialog)
    {
        try
        {
            if (binder.isValid())
            {
                User user = new User();
                binder.writeBean(user);
                users.save(user);
                dialog.close();
            }
            binder.validate();
        } catch (ValidationException ex)
        {
            ex.printStackTrace();
        }
    }

    private void resetCache(CacheManager cacheManager)
    {
        for (String name : cacheManager.getCacheNames())
        {
            Cache cache = cacheManager.getCache(name);
            if (cache != null)
                cache.clear();
        }
    }
}