package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

@Component
public interface UserStorage {
    public Collection<User> findAll();
    public User findById(Long id);
    public User create(User user) throws ValidationException;
    public User update(User user) throws ValidationException;
}
