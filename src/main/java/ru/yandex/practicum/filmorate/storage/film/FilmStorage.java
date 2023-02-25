package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
@Component
public interface FilmStorage {
    public Collection<Film> findAll();
    public Film findById(Long id);
    public Film create(Film film) throws ValidationException;
    public Film update(Film film) throws ValidationException;

}

