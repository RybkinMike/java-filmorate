package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Item;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Controller <T extends Item> {
    protected final Map<Long, T> items = new HashMap();
    private long counter = 0L;

    public Collection<T> findAll () {
        return items.values();
    }

    public T create (final T item) throws ValidationException {
        validateForPost(item);
        item.setId(++counter);
        items.put(item.getId(), item);
        return  item;
    }

    public T update (final T item) throws ValidationException {
        Long id = validateForPut(item);
        items.put(id, item);
        return  item;
    }

    abstract void validate (T item) throws ValidationException;
    abstract void validateForPost (T item) throws ValidationException;
    abstract Long validateForPut (T item) throws ValidationException;
}
