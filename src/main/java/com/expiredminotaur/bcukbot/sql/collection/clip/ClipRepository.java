package com.expiredminotaur.bcukbot.sql.collection.clip;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ClipRepository extends PagingAndSortingRepository<Clip, Integer>
{
    @Override
    @Cacheable(value = "Clips")
    List<Clip> findAll();

    @Override
    @CacheEvict(value = "Clips", allEntries = true)
    void deleteById(Integer ClipId);

    @Override
    @CacheEvict(value = "Clips", allEntries = true)
    void delete(Clip Clip);

    @Override
    @CacheEvict(value = "Clips", allEntries = true)
    Clip save(Clip Clip);
}