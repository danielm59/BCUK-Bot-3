package com.expiredminotaur.bcukbot.sql.twitch.cache.game;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TwitchGameRepository extends CrudRepository<TwitchGame, String>
{
    @Override
    @Cacheable(value = "TwitchGame")
    List<TwitchGame> findAll();

    @Override
    @CacheEvict(value = "TwitchGame", allEntries = true)
    void deleteById(String userID);

    @Override
    @CacheEvict(value = "TwitchGame", allEntries = true)
    void delete(TwitchGame game);

    @Override
    @CacheEvict(value = "TwitchGame", allEntries = true)
    TwitchGame save(TwitchGame game);
}
