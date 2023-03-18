package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmDbStorage {
    public Collection<Film> findAll();
    public Optional<Film> findById(Long id);
    public Film create(Film film) throws ValidationException;
    public Film update(Film film) throws ValidationException;
    Optional <Long> findIdByName(String filmName);
    void addLike(Long filmId, Long userId) throws ValidationException;
    void removeLike(Long userId) throws ValidationException;

    Set<Long> getLikesById(Long filmId);

    List<Film> getPopular(Integer count);
}
