package com.expiredminotaur.bcukbot.sql.twitch.streams.group;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer>
{
    @Override
    @Cacheable(value = "Streams")
    List<Group> findAll();

    @Override
    @CacheEvict(value = "Streams", allEntries = true)
    void deleteById(Integer GroupId);

    @Override
    @CacheEvict(value = "Streams", allEntries = true)
    void delete(Group Group);

    @Override
    @CacheEvict(value = "Streams", allEntries = true)
    Group save(Group Group);
}
