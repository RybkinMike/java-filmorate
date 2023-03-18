package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {
    UserDbStorage storage;

    @Autowired
    public UserService(UserDbStorage storage) {
        this.storage = storage;
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }
    public User findById(Long id) throws ValidationException {
        if (storage.findById(id).isEmpty()) {
            throw new NotFoundException("Пользовать с таким ID не найден.");
        }
        else {
            return storage.findById(id).get();
        }
    }
    public User create(User user) throws ValidationException {
        validateForPost(user);
        return storage.create(user);
    }
    public User update(User user) throws ValidationException {
        validateForPut(user);
        return storage.update(user);
    }

    public void validate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Указан пустой Email");
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            log.warn("Указан неверный Email");
            throw new ValidationException("Адрес электронной почты имеет неправильный формат.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Указан пустой логин");
            throw new ValidationException("Логин не может быть пустым.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Указана неверная дата рождения");
            throw new ValidationException("Приходи, после того как родишься.");
        }
    }


    public void validateForPost(User user) throws ItemAlreadyExistException, ValidationException {
        validate(user);
        Optional<User> userOpt = findByEmail(user.getEmail());
        if (userOpt.isPresent()) {
            log.warn("Попытка внести уже зарегестрированного пользователя");
            throw new ItemAlreadyExistException("Пользователь с электронной почтой " + user.getEmail() + " уже зарегистрирован.");
        }
    }

    public void validateForPut(User user) throws NotFoundException, ValidationException {
        validate(user);
        if (storage.findById(user.getId()).isEmpty()) {
            log.warn("Попытка отредактировать незарегестрированного пользователя");
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден.");
        }
    }

    public void addFriend (Long userId, Long friendId) throws NotFoundException {
        if (storage.findById(userId).isEmpty()) {
            log.warn("Попытка отредактировать незарегестрированного пользователя");
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }
        if (storage.findById(friendId).isEmpty()) {
            log.warn("Попытка отредактировать незарегестрированного пользователя");
            throw new NotFoundException("Пользователь с ID " + friendId + " не найден.");
        }
        if (!storage.containsInFollowers(userId, friendId)) {
            storage.addFriend(friendId, userId);
            log.info("Пользователи с ID {} и {} теперь друзья", userId, friendId);
        }
       /* else {
            storage.addFollower (userId, friendId);
            log.info("Пользователи с ID {} подписался на пользователя с ID {}", userId, friendId);
        }*/
    }

    public void removeFriend (Long userId, Long friendId) throws ValidationException {
        if (storage.removeFriend(userId, friendId)) {
            //storage.addFollower (friendId, userId);
        }
        else {
            throw new ValidationException("Пользователи не были друзьями.");
        }
    }

    public Collection<User> getMutualFriends (Long userId, Long friendId) {
        return storage.getMutualFriends(userId, friendId);
    }

    public Collection<User> getAllFriends (Long userId) {
        return storage.getAllFriends(userId);
    }

    public Optional<User> findByEmail (String userEmail) {
       return storage.findByEmail(userEmail);
    }
}
