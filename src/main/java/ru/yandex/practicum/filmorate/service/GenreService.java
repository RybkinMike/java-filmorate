package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.*;

@Slf4j
@Service
public class GenreService {
    GenreDbStorage storage;

    @Autowired
    public GenreService(GenreDbStorage storage) {
        this.storage = storage;
    }

    public void validate(Genre genre) throws ValidationException {
        if (genre.getName() == null || genre.getName().isBlank()) {
            log.warn("Указано пустое название");
            throw new ValidationException("Название жанра не может быть пустым.");
        }
    }

    public void validateForPost(Genre genre) throws ValidationException, ItemAlreadyExistException {
        validate(genre);
    }

    public Collection<Genre> findAll() {
        return storage.findAll();
    }

    public Genre findById(Long id) {
        if (storage.findById(id).isEmpty()) {
            throw new NotFoundException("Жанр с таким ID не найден.");
        }
        else {
            return storage.findById(id).get();
        }
    }

    public Genre create(Genre genre) throws ValidationException {
        return storage.create(genre);
    }

    public Genre update(Genre genre) throws ValidationException {
        return storage.update(genre);
    }
}
