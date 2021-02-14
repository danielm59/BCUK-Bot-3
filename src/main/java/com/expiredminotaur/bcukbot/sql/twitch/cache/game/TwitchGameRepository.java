package com.expiredminotaur.bcukbot.sql.twitch.cache.game;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TwitchGameRepository extends CrudRepository<TwitchGame, String>
{
    @Override
    @Cacheable(value = "TwitchGame")
    @NotNull
    List<TwitchGame> findAll();

    @Override
    @CacheEvict(value = "TwitchGame", allEntries = true)
    void deleteById(@NotNull String gameID);

    @Override
    @CacheEvict(value = "TwitchGame", allEntries = true)
    void delete(@NotNull TwitchGame game);

    @Override
    @CacheEvict(value = "TwitchGame", allEntries = true)
    @NotNull
    <S extends TwitchGame> S save(@NotNull S game);
}
