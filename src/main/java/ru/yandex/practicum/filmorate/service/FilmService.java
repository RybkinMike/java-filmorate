package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;

@Slf4j
@Service
public class FilmService  {
    UserDbStorage userStorage;
    FilmDbStorage storage;

    @Autowired
    public FilmService(FilmDbStorage storage, UserDbStorage userStorage) {
        this.userStorage = userStorage;
        this.storage = storage;
    }

    public void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Указано пустое название");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Указано слишком длинное описание");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(Film.FIRST_FILM)) {
            log.warn("Указана неверная дата релиза");
            throw new ValidationException("Прости, но братья Люмьер были первыми");
        }
        if (film.getDuration() < 1) {
            log.warn("Указана неверная продолжительность");
            throw new ValidationException("Ну хоть одну секундочку должен фильм идти");
        }
    }

    public void validateForPost(Film film) throws ValidationException, ItemAlreadyExistException {
        validate(film);
    }


    public Long validateForPut(Film film) throws ValidationException, NotFoundException {
        validate(film);
        log.info("Фильм {} бновлен.", film.getId());
        if (storage.findById(film.getId()).isEmpty()) {
            log.warn("Попытка отредактировать незарегестрированный фильм");
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден.");
        }
        log.info("Фильм {} бновлен.", film.getName());
        return film.getId();
    }

    public Collection<Film> findAll() {
        return storage.findAll();
    }
    public Film findById(Long id) {
        if (storage.findById(id).isEmpty()) {
            throw new NotFoundException("Фильм с таким ID не найден.");
        }
        else {
            return storage.findById(id).get();
        }
    }

    public Film findByName(String name) {
        return findById(storage.findIdByName(name).get());
    }

    public Film create(Film film) throws ValidationException {
        validateForPost(film);
        return storage.create(film);
    }

    public Film update(Film film) throws ValidationException {
        validateForPut(film);
        return storage.update(film);
    }

    public void addLike (Long filmId, Long userId) throws ValidationException {
        storage.addLike(filmId, userId);
    }

    public void removeLike (Long filmId, Long userId) throws ValidationException {
        storage.removeLike(userId);
    }

    public List<Film> getPopular (Integer count){
        return storage.getPopular(count);
    }


    public Optional<Long> findIdByName (String filmName) {
        return storage.findIdByName(filmName);
    }

}
