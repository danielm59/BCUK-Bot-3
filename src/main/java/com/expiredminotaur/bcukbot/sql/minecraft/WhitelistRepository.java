package com.expiredminotaur.bcukbot.sql.minecraft;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WhitelistRepository extends CrudRepository<Whitelist, Long>
{
    @Override
    @Cacheable(value = "MC_WHITELIST")
    List<Whitelist> findAll();

    @Override
    @CacheEvict(value = "MC_WHITELIST", allEntries = true)
    void deleteById(Long id);

    @Override
    @CacheEvict(value = "MC_WHITELIST", allEntries = true)
    void delete(Whitelist whitelist);

    @Override
    @CacheEvict(value = "MC_WHITELIST", allEntries = true)
    Whitelist save(Whitelist whitelist);
}