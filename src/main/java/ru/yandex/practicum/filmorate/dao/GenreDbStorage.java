package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreDbStorage {
    public Collection<Genre> findAll();
    Genre getGenre(ResultSet rs) throws SQLException;
    public Optional<Genre> findById(long id);
    public Genre create(Genre genre) throws ValidationException;
    public Genre update(Genre genre) throws ValidationException;
    List<Genre> getGenresByFilmId(long id);
}