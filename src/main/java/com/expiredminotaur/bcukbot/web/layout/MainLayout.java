package com.expiredminotaur.bcukbot.web.layout;

import com.expiredminotaur.bcukbot.web.view.MainView;
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
import com.expiredminotaur.bcukbot.web.view.settings.MusicSettingsView;
import com.expiredminotaur.bcukbot.web.view.settings.SFXView;
import com.expiredminotaur.bcukbot.web.view.settings.StreamAnnouncementsView;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(value = Lumo.class, variant = Lumo.DARK)
@PWA(name = "BCUK Bot",
        shortName = "BCUK Bot",
        description = "BCUK Twitch and Discord Bot.",
        enableInstallPrompt = false)
public class MainLayout extends AppLayout
{
    private final Div childWrapper = new Div();

    public MainLayout()
    {
        MenuBar menu = new MenuBar();
        String resolvedImage = VaadinService.getCurrent().resolveResource(
                "img/BCUK.png", VaadinSession.getCurrent().getBrowser());
        Image logo = new Image(resolvedImage, "logo");
        menu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);

        menu.addItem("Home", e -> UI.getCurrent().navigate(MainView.class));
        MenuItem bots = menu.addItem("Bots");
        menu.addItem("Music", e -> UI.getCurrent().navigate(MusicView.class));
        MenuItem collections = menu.addItem("Collections");
        MenuItem settings = menu.addItem("Settings");
        menu.addItem("Logout", e -> UI.getCurrent().getPage().setLocation("/logout"));

        SubMenu botsSubMenu = bots.getSubMenu();
        botsSubMenu.addItem("Discord", e -> UI.getCurrent().navigate(DiscordBotView.class));
        botsSubMenu.addItem("Twitch", e -> UI.getCurrent().navigate(TwitchBotView.class));
        botsSubMenu.addItem("Commands", e -> UI.getCurrent().navigate(CommandsView.class));

        SubMenu collectionsSubMenu = collections.getSubMenu();
        collectionsSubMenu.addItem("Quotes", e -> UI.getCurrent().navigate(QuoteView.class));
        collectionsSubMenu.addItem("Jokes", e -> UI.getCurrent().navigate(JokeView.class));
        collectionsSubMenu.addItem("Clips", e -> UI.getCurrent().navigate(ClipView.class));

        SubMenu settingSubMenu = settings.getSubMenu();
        settingSubMenu.addItem("Stream Announcements", e -> UI.getCurrent().navigate(StreamAnnouncementsView.class));
        settingSubMenu.addItem("Counters", e -> UI.getCurrent().navigate(CountersView.class));
        settingSubMenu.addItem("SFX", e -> UI.getCurrent().navigate(SFXView.class));
        settingSubMenu.addItem("Music", e -> UI.getCurrent().navigate(MusicSettingsView.class));
        settingSubMenu.addItem("Database", e -> UI.getCurrent().navigate(DatabaseView.class));
        settingSubMenu.addItem("Alias", e -> UI.getCurrent().navigate(AliasView.class));
        settingSubMenu.addItem("Banned Phrases", e -> UI.getCurrent().navigate(BannedPhrasesView.class));

        addToNavbar(true, logo, menu);

        childWrapper.setHeightFull();

        setContent(childWrapper);
    }

    @Override
    public void showRouterLayoutContent(HasElement content)
    {
        childWrapper.getElement().appendChild(content.getElement());
    }
}
