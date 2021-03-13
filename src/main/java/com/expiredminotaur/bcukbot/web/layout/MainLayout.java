package com.expiredminotaur.bcukbot.web.layout;

import com.expiredminotaur.bcukbot.web.security.SecurityUtils;
import com.expiredminotaur.bcukbot.web.security.UserTools;
import com.expiredminotaur.bcukbot.web.view.MainView;
import com.expiredminotaur.bcukbot.web.view.MinecraftWhitelistView;
import com.expiredminotaur.bcukbot.web.view.MusicView;
import com.expiredminotaur.bcukbot.web.view.bot.CommandsView;
import com.expiredminotaur.bcukbot.web.view.bot.DiscordBotView;
import com.expiredminotaur.bcukbot.web.view.bot.TwitchBotView;
import com.expiredminotaur.bcukbot.web.view.collection.ClipView;
import com.expiredminotaur.bcukbot.web.view.collection.JokeView;
import com.expiredminotaur.bcukbot.web.view.collection.QuoteView;
import com.expiredminotaur.bcukbot.web.view.settings.AliasView;
import com.expiredminotaur.bcukbot.web.view.settings.BannedPhrasesView;
import com.expiredminotaur.bcukbot.web.view.settings.CountersView;
import com.expiredminotaur.bcukbot.web.view.settings.DatabaseView;
import com.expiredminotaur.bcukbot.web.view.settings.JustGivingView;
import com.expiredminotaur.bcukbot.web.view.settings.MusicSettingsView;
import com.expiredminotaur.bcukbot.web.view.settings.SFXView;
import com.expiredminotaur.bcukbot.web.view.settings.StreamAnnouncementsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;

@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainLayout extends AppLayout
{
    private final Div childWrapper = new Div();
    private final UserTools userTools;

    @Autowired
    public MainLayout(UserTools userTools)
    {
        this.userTools = userTools;
        MenuBar menu = new MenuBar();
        String resolvedImage = VaadinService.getCurrent().resolveResource(
                "img/BCUK.png", VaadinSession.getCurrent().getBrowser());
        Image logo = new Image(resolvedImage, "logo");
        menu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);

        menu.addItem("Home", e -> UI.getCurrent().navigate(MainView.class));
        if (userTools.isCurrentUserMod() || userTools.isCurrentUserAdmin())
        {
            setupBots(menu.addItem("Bots").getSubMenu());
        }
        menu.addItem("Music", e -> UI.getCurrent().navigate(MusicView.class));
        MenuItem collections = menu.addItem("Collections");
        if (SecurityUtils.isAccessGranted(MinecraftWhitelistView.class, userTools))
            menu.addItem("Minecraft Whitelist", e -> UI.getCurrent().navigate(MinecraftWhitelistView.class));
        if (userTools.isCurrentUserMod() || userTools.isCurrentUserAdmin())
        {
            setupSettings(menu.addItem("Settings").getSubMenu());
        }
        menu.addItem("Logout", e -> UI.getCurrent().getPage().setLocation("/logout"));

        SubMenu collectionsSubMenu = collections.getSubMenu();
        collectionsSubMenu.addItem("Quotes", e -> UI.getCurrent().navigate(QuoteView.class));
        collectionsSubMenu.addItem("Jokes", e -> UI.getCurrent().navigate(JokeView.class));
        collectionsSubMenu.addItem("Clips", e -> UI.getCurrent().navigate(ClipView.class));

        addToNavbar(true, logo, menu);

        childWrapper.setHeightFull();

        setContent(childWrapper);
    }

    private void setupBots(SubMenu botsSubMenu)
    {
        addSubMenuItem(botsSubMenu,"Discord", DiscordBotView.class);
        addSubMenuItem(botsSubMenu,"Twitch", TwitchBotView.class);
        addSubMenuItem(botsSubMenu,"Commands", CommandsView.class);
    }

    private void setupSettings(SubMenu settingSubMenu)
    {
        addSubMenuItem(settingSubMenu, "Stream Announcements", StreamAnnouncementsView.class);
        addSubMenuItem(settingSubMenu, "Counters", CountersView.class);
        addSubMenuItem(settingSubMenu, "SFX", SFXView.class);
        addSubMenuItem(settingSubMenu, "Music", MusicSettingsView.class);
        addSubMenuItem(settingSubMenu, "Database", DatabaseView.class);
        addSubMenuItem(settingSubMenu, "Alias", AliasView.class);
        addSubMenuItem(settingSubMenu, "Banned Phrases", BannedPhrasesView.class);
        addSubMenuItem(settingSubMenu, "JustGiving", JustGivingView.class);
    }

    private void addSubMenuItem(SubMenu subMenu, String name, Class<? extends Component> view)
    {
        if (SecurityUtils.isAccessGranted(view, userTools))
            subMenu.addItem(name, e -> UI.getCurrent().navigate(view));
    }

    @Override
    public void showRouterLayoutContent(HasElement content)
    {
        childWrapper.getElement().appendChild(content.getElement());
    }
}
