package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.Collection;

@Slf4j
@Component
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрошены все пользователи");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable("id") Long id) throws ValidationException {
        log.info("Запрошен пользователь {}", id);
        return userService.findById(id);
    }

    @PostMapping
    public User create(@RequestBody @Valid @Email User user) throws ValidationException {
        log.info("Запрос на добавление пользователя {}.", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid @Email User user) throws ValidationException {
        log.info("Запрос на обновление данных пользователя {}", user);
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с ID {} хочет добавить в друзья пользователя с ID {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping ("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) throws ValidationException {
        log.info("Пользователь с ID {} хочет удалилить из друзей пользователя с ID {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriends(@PathVariable("id") Long id){
        log.info("Запрос друзей пользователя с ID {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId){
        log.info("Запрос общих друзей пользователя с ID {} и пользователя с ID {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }
}