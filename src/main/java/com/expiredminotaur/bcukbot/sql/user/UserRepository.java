package com.expiredminotaur.bcukbot.sql.user;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long>
{
    @Override
    @Cacheable(value = "Users")
    List<User> findAll();

    @Override
    @Cacheable(value = "Users")
    Optional<User> findById(Long UserId);

    @Query("from User where isTwitchBotEnabled=1")
    @Cacheable(value = "Users")
    List<User> chatBotUsers();

    @Override
    @CacheEvict(value = "Users", allEntries = true)
    void deleteById(Long UserId);

    @Override
    @CacheEvict(value = "Users", allEntries = true)
    void delete(User user);

    @Override
    @CacheEvict(value = "Users", allEntries = true)
    User save(User user);
}