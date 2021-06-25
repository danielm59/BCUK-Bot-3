package com.expiredminotaur.bcukbot.web.view.stream;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.atmosphere.cpr.Broadcaster;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Push
@Route("/stream")
public class StreamView extends HorizontalLayout
{
    private final Paragraph p = new Paragraph();
    private Registration broadcasterRegistration;

    public StreamView()
    {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        p.getStyle().set("font-size", "10em")
                .set("text-align", "center");
        p.setWidthFull();
        add(p);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Service.register(newMessage ->
                ui.access(() -> p.setText(newMessage)));
        ui.access(() -> p.setText(Service.getLastMessage()));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    public static class Service
    {
        private static final Executor executor = Executors.newSingleThreadExecutor();

        private static final LinkedList<Consumer<String>> listeners = new LinkedList<>();

        private static String lastMessage;

        public static synchronized Registration register(
                Consumer<String> listener)
        {
            listeners.add(listener);

            return () ->
            {
                synchronized (Broadcaster.class)
                {
                    listeners.remove(listener);
                }
            };
        }

        public static synchronized void broadcast(String message)
        {
            lastMessage = message;
            for (Consumer<String> listener : listeners)
            {
                executor.execute(() -> listener.accept(message));
            }
        }

        public static String getLastMessage()
        {
            return lastMessage;
        }
    }
}