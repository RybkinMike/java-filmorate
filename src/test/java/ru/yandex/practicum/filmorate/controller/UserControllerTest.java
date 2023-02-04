package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static Controller controller;
    User user1;
    User user2;

    @BeforeEach
    public void beforeEach() throws ValidationException {
        controller = new UserController();
        user1 = new User("ya@yandex.ru", "Начинающий программист", "Михаил", LocalDate.of(1987,4,18));
        controller.create(user1);
        user2 = new User("ya@mail.ru", "Сеньор-помидор", "Артем", LocalDate.of(1999,1,12));;
    }

    @Test
    void findAllTest() {
        Collection<User> users = controller.findAll();
        assertNotNull(users, "Контроллер пустой.");
        assertEquals(1, users.size(), "Неверное количество итемов.");
    }

    @Test
    void create() throws ValidationException {
        Collection<User> users = controller.findAll();
        assertEquals(1, users.size(), "Неверное количество фильмов.");
        controller.create(user2);
        Collection<User> users2 = controller.findAll();
        assertEquals(2, users2.size(), "Неверное количество фильмов.");
        assertTrue(users2.contains(user1), "Фильм1 отсутствует.");
        assertTrue(users2.contains(user2), "Фильм2 отсутствует.");
    }

    @Test
    void update() throws ValidationException {
        Collection<User> users = controller.findAll();
        assertEquals(1, users.size(), "Неверное количество фильмов.");
        User userUpdate = new User("ya@yandex.ru", "Начинающий программист", "Михалыч", LocalDate.of(1987,4,18));
        userUpdate.setId(1);
        controller.update(userUpdate);
        Collection<User> users2 = controller.findAll();
        System.out.println(users2);
        assertEquals(1, users2.size(), "Неверное количество фильмов.");
        assertTrue(users2.contains(userUpdate), "Фильм1 не обновился.");
    }

    @Test
    void shouldTrowExceptionIfEmailIsBlank() throws ValidationException {
        User user3 = new User(" ", "Начинающий программист", "Михалыч", LocalDate.of(1987,4,18));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Адрес электронной почты не может быть пустым.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfEmailIsWrong() throws ValidationException {
        User user3 = new User("112", "Начинающий программист", "Михалыч", LocalDate.of(1987,4,18));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Адрес электронной почты имеет неправильный формат.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfLoginIsBlank() throws ValidationException {
        User user3 = new User("1@1.ru ", "  ", "Михалыч", LocalDate.of(1987,4,18));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Логин не может быть пустым.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfBirthdayAfterNow() throws ValidationException {
        User user3 = new User("1@1.ru ", "111", "Михалыч", LocalDate.of(2023,2,18));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Приходи, после того как родишься.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldNamedByLogin() throws ValidationException {
        User user3 = new User("1@1.ru", "111", " ", LocalDate.of(2023,2,18));
        assertEquals(user3.getLogin(), user3.getName(), "Имя по логину не присвоилось");
    }

    @Test
    void shouldTrowExceptionIfUpdateUserThatNotContains() throws ValidationException {
        User user3 = new User("1@1.ru", "111", "Михалыч", LocalDate.of(2022,2,18));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.update(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Пользователь с ID 0 не найден.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfCreateUserThatContains() throws ValidationException {
        User user3 = new User("ya@yandex.ru", "111", "Михалыч", LocalDate.of(2022,2,18));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Пользователь с электронной почтой ya@yandex.ru уже зарегистрирован.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }
}