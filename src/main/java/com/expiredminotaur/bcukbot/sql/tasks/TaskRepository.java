package com.expiredminotaur.bcukbot.sql.tasks;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long>
{
    @Override
    @NotNull
    List<Task> findAll();

    List<Task> findByCompleted(boolean isCompleted);
}
