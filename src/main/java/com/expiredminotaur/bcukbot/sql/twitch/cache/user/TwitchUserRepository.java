package com.expiredminotaur.bcukbot.sql.twitch.cache.user;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TwitchUserRepository extends CrudRepository<TwitchUser, String>
{
    @Override
    @Cacheable(value = "TwitchUser")
    List<TwitchUser> findAll();

    @Override
    @CacheEvict(value = "TwitchUser", allEntries = true)
    void deleteById(String userID);

    @Override
    @CacheEvict(value = "TwitchUser", allEntries = true)
    void delete(TwitchUser user);

    @Override
    @CacheEvict(value = "TwitchUser", allEntries = true)
    TwitchUser save(TwitchUser user);
}
