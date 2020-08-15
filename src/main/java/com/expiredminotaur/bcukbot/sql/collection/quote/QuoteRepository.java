package com.expiredminotaur.bcukbot.sql.collection.quote;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface QuoteRepository extends PagingAndSortingRepository<Quote, Integer>
{
    @Override
    @Cacheable(value = "Quotes")
    List<Quote> findAll();

    @Override
    @CacheEvict(value = "Quotes", allEntries = true)
    void deleteById(Integer QuoteId);

    @Override
    @CacheEvict(value = "Quotes", allEntries = true)
    void delete(Quote quote);

    @Override
    @CacheEvict(value = "Quotes", allEntries = true)
    Quote save(Quote Quote);
}
