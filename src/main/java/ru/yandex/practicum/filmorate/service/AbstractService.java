package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Item;
import ru.yandex.practicum.filmorate.storage.Storage;
import java.util.*;

@Service
public abstract class AbstractService<T extends Item> {
    Storage<T> storage;
    public Collection<T> findAll() {
        return storage.findAll();
    }

    public T create(T item) throws ValidationException {
        validateForPost(item);
        return storage.create(item);
    }

    public T findById(Long id){
        return storage.findById(id);
    }

    public T update(T item) throws ValidationException {
        validateForPut(item);
        return storage.update(item);
    }

    abstract void validate(T item) throws ValidationException;
    abstract public void validateForPost(T item) throws ValidationException;
    abstract public Long validateForPut(T item) throws ValidationException;

}
