package com.expiredminotaur.bcukbot.command;

import reactor.core.publisher.Mono;

public abstract class CommandEvent<T>
{
    protected final T event;
    private String aliased = null;

    public CommandEvent(T event)
    {
        this.event = event;
    }

    public void setAliased(String aliased)
    {
        this.aliased = aliased;
    }

    public T getEvent()
    {
        return event;
    }

    public String getFinalMessage()
    {
        return aliased != null ? aliased : getOriginalMessage();
    }

    public abstract String getOriginalMessage();

    public abstract Mono<Void> respond(String message);

    public Mono<Void> empty()
    {
        return Mono.empty();
    }

    public abstract String getSourceName();
}
