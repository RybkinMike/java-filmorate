package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface MpaDbStorage {
    Collection<Mpa> findAll();
    Mpa getMpa(ResultSet rs) throws SQLException;
    Optional<Mpa> findById(long id);
    Mpa create(Mpa mpa) throws ValidationException;
    Mpa update(Mpa mpa) throws ValidationException;

}