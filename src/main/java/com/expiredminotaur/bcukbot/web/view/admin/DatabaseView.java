package com.expiredminotaur.bcukbot.web.view.admin;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Route(value = "database", layout = MainLayout.class)
@AccessLevel(Role.ADMIN)
public class DatabaseView extends VerticalLayout
{
    public DatabaseView(@Autowired UserRepository users, @Autowired CacheManager cacheManager)
    {
        Button resetCacheButton = new Button("Reset Cache", e -> resetCache(cacheManager));
        add(resetCacheButton);
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