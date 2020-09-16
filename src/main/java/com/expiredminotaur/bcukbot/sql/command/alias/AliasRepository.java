package com.expiredminotaur.bcukbot.sql.command.alias;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO add a view to manage these
public interface AliasRepository extends CrudRepository<Alias, Integer>
{
    @Override
    @Cacheable(value = "Alias")
    List<Alias> findAll();

    @Query("from Alias where lower(short_command)=:command")
    @Cacheable(value = "Alias")
    List<Alias> findByTrigger(String command);

    @Override
    @CacheEvict(value = "Alias", allEntries = true)
    void deleteById(Integer id);

    @Override
    @CacheEvict(value = "Alias", allEntries = true)
    void delete(Alias alias);

    @Override
    @CacheEvict(value = "Alias", allEntries = true)
    Alias save(Alias alias);
}
