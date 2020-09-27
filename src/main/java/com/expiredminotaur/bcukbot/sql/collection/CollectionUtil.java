package com.expiredminotaur.bcukbot.sql.collection;

import com.expiredminotaur.bcukbot.command.CommandEvent;

public abstract class CollectionUtil
{
    public <R> R processCommand(CommandEvent<?, R> event)
    {
        String[] args = event.getFinalMessage().split(" ", 3);
        if (args.length > 1)
        {
            if (args[1].toLowerCase().equals("add"))
                return event.respond(add(args[2], event.getSourceName()));
            try
            {
                int id = Integer.parseInt(args[1]);
                return event.respond(get(id));
            } catch (Exception ignore)
            {
            }
        }
        return event.respond(random());
    }

    public abstract String add(String newEntry, String source);

    public abstract String get(int id);

    public abstract String random();
}
