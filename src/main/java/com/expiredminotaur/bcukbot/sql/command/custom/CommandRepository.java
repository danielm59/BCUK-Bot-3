package com.expiredminotaur.bcukbot.sql.command.custom;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandRepository extends CrudRepository<CustomCommand, Integer>
{
    @Override
    @Cacheable(value = "CustomCommand")
    List<CustomCommand> findAll();

    @Query("from CustomCommand where isDiscordEnabled=true and lower(triggerString)=:trigger")
    @Cacheable(value = "CustomCommand")
    CustomCommand findDiscord(String trigger);

    @Query("from CustomCommand c, in (c.twitchEnabledUsers) u where lower(u.twitchName)=:channel and c.triggerString=:trigger")
    @Cacheable(value = "CustomCommand")
    CustomCommand findTwitch(String channel, String trigger);

    @Override
    @CacheEvict(value = "CustomCommand", allEntries = true)
    void deleteById(Integer commandId);

    @Override
    @CacheEvict(value = "CustomCommand", allEntries = true)
    void delete(CustomCommand command);

    @Override
    @CacheEvict(value = "CustomCommand", allEntries = true)
    CustomCommand save(CustomCommand command);
}
