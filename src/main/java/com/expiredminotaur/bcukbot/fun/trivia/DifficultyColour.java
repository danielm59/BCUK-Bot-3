package com.expiredminotaur.bcukbot.fun.trivia;

import discord4j.rest.util.Color;

public enum DifficultyColour
{
    EASY("easy", Color.of(0, 255, 0)),
    MEDIUM("medium", Color.of(255, 255, 0)),
    HARD("hard", Color.of(255, 0, 0));

    private final String name;
    final Color color;

    DifficultyColour(String name, Color color)
    {
        this.name = name;
        this.color = color;
    }

    public static DifficultyColour fromString(String text)
    {
        for (DifficultyColour c : DifficultyColour.values())
        {
            if (c.name.equalsIgnoreCase(text))
            {
                return c;
            }
        }
        return null;
    }
}
