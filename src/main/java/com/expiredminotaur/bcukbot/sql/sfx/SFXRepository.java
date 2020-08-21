package com.expiredminotaur.bcukbot.sql.sfx;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SFXRepository extends CrudRepository<SFX, Integer>
{
    @Override
    @Cacheable(value = "SFX")
    List<SFX> findAll();

    @Query("from SFX where lower(trigger_command)=:trigger")
    @Cacheable(value = "SFX")
    List<SFX> findByTrigger(String trigger);

    @Override
    @CacheEvict(value = "SFX", allEntries = true)
    void deleteById(Integer id);

    @Override
    @CacheEvict(value = "SFX", allEntries = true)
    void delete(SFX sfx);

    @Override
    @CacheEvict(value = "SFX", allEntries = true)
    SFX save(SFX sfx);
}
