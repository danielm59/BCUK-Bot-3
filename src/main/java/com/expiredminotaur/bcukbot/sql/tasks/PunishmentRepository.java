package com.expiredminotaur.bcukbot.sql.tasks;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PunishmentRepository extends CrudRepository<Punishment, Long>
{
    @Override
    @NotNull
    List<Punishment> findAll();

    List<Punishment> findByPunishmentGiven(boolean b);
}
