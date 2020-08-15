package com.expiredminotaur.bcukbot.sql.twitch.cache.game;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TwitchGame
{
    @Id
    String gameID;
    String game;

    public String getGameID()
    {
        return gameID;
    }

    public void setGameID(String gameID)
    {
        this.gameID = gameID;
    }

    public String getGame()
    {
        return game;
    }

    public void setGame(String game)
    {
        this.game = game;
    }
}
