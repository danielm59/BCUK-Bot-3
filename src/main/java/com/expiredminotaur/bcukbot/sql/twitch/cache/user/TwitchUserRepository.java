package com.expiredminotaur.bcukbot.sql.twitch.cache.user;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TwitchUserRepository extends CrudRepository<TwitchUser, String>
{
    @Override
    @Cacheable(value = "TwitchUser")
    @NotNull
    List<TwitchUser> findAll();

    @Override
    @CacheEvict(value = "TwitchUser", allEntries = true)
    void deleteById(@NotNull String userID);

    @Override
    @CacheEvict(value = "TwitchUser", allEntries = true)
    void delete(@NotNull TwitchUser user);

    @Override
    @CacheEvict(value = "TwitchUser", allEntries = true)
    @NotNull
    <S extends TwitchUser> S save(@NotNull S user);
}
