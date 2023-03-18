package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.*;

@Slf4j
@Service
public class MpaService {
    MpaDbStorage storage;

    @Autowired
    public MpaService(MpaDbStorage storage) {
        this.storage = storage;
    }

    public void validate(Mpa mpa) throws ValidationException {
        if (mpa.getName() == null || mpa.getName().isBlank()) {
            log.warn("Указано пустое название");
            throw new ValidationException("Название рейтинга не может быть пустым.");
        }

    }

    public void validateForPost(Mpa mpa) throws ValidationException, ItemAlreadyExistException {
        validate(mpa);
    }

    public Collection<Mpa> findAll() {
        return storage.findAll();
    }

    public Mpa findById(Long id) {
        if (storage.findById(id).isEmpty()) {
            throw new NotFoundException("Рейтинг с таким ID не найден.");
        }
        else {
            return storage.findById(id).get();
        }
    }

    public Mpa create(Mpa mpa) throws ValidationException {
        return storage.create(mpa);
    }

    public Mpa update(Mpa mpa) throws ValidationException {
        return storage.update(mpa);
    }
}
