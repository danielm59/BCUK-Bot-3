package com.expiredminotaur.bcukbot.command;

public abstract class CommandEvent<T, R>
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

    public abstract R respond(String message);

    public abstract R empty();

    public abstract String getSourceName();
}
