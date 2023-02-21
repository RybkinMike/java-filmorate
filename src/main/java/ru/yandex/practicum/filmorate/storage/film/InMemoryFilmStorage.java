package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

@Component
public class InMemoryFilmStorage extends Storage<Film> implements FilmStorage {

}

