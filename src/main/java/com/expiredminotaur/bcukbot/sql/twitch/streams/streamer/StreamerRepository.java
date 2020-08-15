package com.expiredminotaur.bcukbot.sql.twitch.streams.streamer;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StreamerRepository extends JpaRepository<Streamer, Integer>
{
    @Override
    @Cacheable(value = "Streams")
    List<Streamer> findAll();

    @Override
    @CacheEvict(value = "Streams", allEntries = true)
    void deleteById(Integer StreamerId);

    @Override
    @CacheEvict(value = "Streams", allEntries = true)
    void delete(Streamer Streamer);

    @Override
    @CacheEvict(value = "Streams", allEntries = true)
    Streamer save(Streamer Streamer);
}
