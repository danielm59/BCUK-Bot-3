package com.expiredminotaur.bcukbot.fun.counters;

import com.expiredminotaur.bcukbot.command.CommandEvent;
import com.expiredminotaur.bcukbot.sql.counter.Counter;
import com.expiredminotaur.bcukbot.sql.counter.CounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CounterHandler
{
    @Autowired
    CounterRepository counterRepository;

    public <R> R processCommand(CommandEvent<?, R> event)
    {
        String message = event.getFinalMessage().split(" ", 2)[0];
        List<Counter> triggers = counterRepository.findByTrigger(message.toLowerCase());
        if (triggers.size() > 0)
        {
            Counter counter = triggers.get(0);
            counter.incrementCurrentValue();
            counterRepository.save(counter);
            return event.respond(counter.getIncrementMessage() + "    "
                    + String.format(counter.getMessage(), counter.getCurrentValue()));
        }
        triggers = counterRepository.findByCheck(message.toLowerCase());
        if (triggers.size() > 0)
        {
            Counter counter = triggers.get(0);
            return event.respond(String.format(counter.getMessage(), counter.getCurrentValue()));
        }
        return event.empty();
    }
}