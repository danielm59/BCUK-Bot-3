package com.expiredminotaur.bcukbot.sql.tasks;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long>
{
    //TODO: add cached functions

    @Override
    List<Task> findAll();
}
