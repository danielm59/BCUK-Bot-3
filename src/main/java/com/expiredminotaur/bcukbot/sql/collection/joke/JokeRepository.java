package com.expiredminotaur.bcukbot.sql.collection.joke;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface JokeRepository extends PagingAndSortingRepository<Joke, Integer>
{
    @Override
    @Cacheable(value = "Jokes")
    List<Joke> findAll();

    @Override
    @CacheEvict(value = "Jokes", allEntries = true)
    void deleteById(Integer JokeId);

    @Override
    @CacheEvict(value = "Jokes", allEntries = true)
    void delete(Joke Joke);

    @Override
    @CacheEvict(value = "Jokes", allEntries = true)
    Joke save(Joke Joke);
}
