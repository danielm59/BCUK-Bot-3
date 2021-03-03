package com.expiredminotaur.bcukbot.discord.command.category;

import com.expiredminotaur.bcukbot.discord.PointsSystem;
import com.expiredminotaur.bcukbot.discord.command.DiscordCommand;
import com.expiredminotaur.bcukbot.discord.command.DiscordPermissions;
import com.expiredminotaur.bcukbot.fun.slot.SlotGame;
import com.expiredminotaur.bcukbot.fun.trivia.TriviaGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GamesCategory extends Category
{
    private PointsSystem pointsSystem;
    private SlotGame slotGame;
    private TriviaGame triviaGame;

    public GamesCategory()
    {
        commands.put("!Points", new DiscordCommand(e -> pointsSystem.points(e), DiscordPermissions::general));
        commands.put("!Slot", new DiscordCommand(e -> slotGame.startGame(e), DiscordPermissions::general));
        commands.put("!Trivia", new DiscordCommand(e -> triviaGame.trivia(e), DiscordPermissions::general));
    }

    @Autowired
    public final void setPointsSystem(PointsSystem pointsSystem)
    {
        this.pointsSystem = pointsSystem;
    }

    @Autowired
    public final void setSlotGame(SlotGame slotGame)
    {
        this.slotGame = slotGame;
    }

    @Autowired
    public final void setTriviaGame(TriviaGame triviaGame)
    {
        this.triviaGame = triviaGame;
    }

    @Override
    public String getName()
    {
        return "GAMES";
    }
}
