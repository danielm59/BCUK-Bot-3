package com.expiredminotaur.bcukbot.sql.twitch.bannedphrase;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BannedPhraseRepository extends CrudRepository<BannedPhrase, Integer>
{
    @Override
    @Cacheable(value = "BannedPhrase")
    List<BannedPhrase> findAll();

    @Override
    @CacheEvict(value = "BannedPhrase", allEntries = true)
    void delete(BannedPhrase phase);

    @Override
    @CacheEvict(value = "BannedPhrase", allEntries = true)
    BannedPhrase save(BannedPhrase phase);
}
