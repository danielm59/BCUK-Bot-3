package com.expiredminotaur.bcukbot.sql.command.custom;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandRepository extends CrudRepository<CustomCommand, Integer>
{
    //TODO: cache

    @Override
    List<CustomCommand> findAll();

    @Query("from CustomCommand where isDiscordEnabled=true and lower(triggerString)=:trigger")
    CustomCommand findDiscord(String trigger);

    @Query("from CustomCommand c, in (c.twitchEnabledUsers) u where lower(u.twitchName)=:channel and c.triggerString=:trigger")
    CustomCommand findTwitch(String channel, String trigger);
}
