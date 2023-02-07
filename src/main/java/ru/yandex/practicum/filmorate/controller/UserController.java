package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends Controller<User> {

    @Override
    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на всех пользователей");
        return super.findAll();
    }

    @Override
    @PostMapping
    public User create(@RequestBody @Valid @Email @NotBlank User user) throws ValidationException {
        log.info("Добавлен пользователь {}", user);
        return super.create(user);
    }

    @Override
    @PutMapping
    public User update(@RequestBody @Valid @Email @NotBlank User user) throws ValidationException {
        log.info("Данные пользователя {} обновлены", user);
        return super.update(user);
    }

    @Override
    void validate(User user) throws ValidationException {
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Указан пустой Email");
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if(!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            log.warn("Указан неверный Email");
            throw new ValidationException("Адрес электронной почты имеет неправильный формат.");
        }
        if(user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Указан пустой логин");
            throw new ValidationException("Логин не может быть пустым.");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Указана неверная дата рождения");
            throw new ValidationException("Приходи, после того как родишься.");
        }
    }

    @Override
    void validateForPost (User user) throws ValidationException {
        validate(user);
        for (User userInUsers: items.values()) {
            if (userInUsers.getEmail().equals(user.getEmail())) {
                log.warn("Попытка внести уже зарегистрираванного пользователя");
                throw new ValidationException("Пользователь с электронной почтой " + user.getEmail() + " уже зарегистрирован.");
            }
        }
    }
    @Override
    Long validateForPut(User user) throws ValidationException {
        validate(user);
        if (!items.containsKey(user.getId())) {
            log.warn("Попытка отредактировать незарегестрированного пользователя");
            throw new ValidationException("Пользователь с ID " + user.getId() + " не найден.");
        }
        Long id = user.getId();
        return id;
    }
}
