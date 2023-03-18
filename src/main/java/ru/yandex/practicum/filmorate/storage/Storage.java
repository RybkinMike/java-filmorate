package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Item;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public abstract class Storage <T extends Item> {
    protected final Map<Long, T> items = new HashMap();
    private long counter = 0L;

    public Collection<T> findAll() {
        return items.values();
    }

    public T findById(Long id){
        if (!items.containsKey(id)) {
            log.warn("Неправильный ID");
            throw new NotFoundException("Данные по " + id + " не найден.");
        }
        return items.get(id);
    }

    public T create(final T item) {
        item.setId(++counter);
        items.put(item.getId(), item);
        return  item;
    }

    public T update(final T item) {
        items.put(item.getId(), item);
        return  item;
    }
}