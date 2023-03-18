package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import java.time.LocalDate;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
   //private final UserDbStorage userStorage;
    private final UserController controller;

    @Test
    void createAndFindAllTest() throws ValidationException {
        Collection<User> users = controller.findAll();
        int n = users.size();
        User user1 = new User("ya@yandex.ru", "Начинающий программист", "Михаил", LocalDate.of(1987,4,18));
        controller.create(user1);
        Collection<User> users1 = controller.findAll();
        assertNotNull(users, "Контроллер пустой.");
        assertEquals(n+1, users1.size(), "Неверное количество итемов.");
    }

    @Test
    void findByIdTest() throws ValidationException {
        User user = controller.findUser(1L);
        User user1 = new User("User1@mail.ru", "LogUser1", "User#1", LocalDate.of(2020,12,31));
        assertEquals(user, user1, "Пользователи не совпали.");
    }

    @Test
    void update() throws ValidationException {
        Collection<User> users = controller.findAll();
        int n = users.size();
        User user1 = new User("User2@mail.ru", "UpdateLogUser2", "UpdateUser#2", LocalDate.of(2021,12,31));
        user1.setId(2L);
        controller.update(user1);
        Collection<User> users1 = controller.findAll();
        assertNotNull(users, "Контроллер пустой.");
        assertEquals(n, users1.size(), "Неверное количество итемов.");
        assertTrue(users1.contains(user1), "Пользователь2 не обновился.");
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
        User user3 = new User("1@1.ru ", "111", "Михалыч", LocalDate.of(2023,5,18));
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
    void shouldTrowExceptionIfUpdateUserThatNotContains() throws NotFoundException {
        User user3 = new User("1@1.ru", "111", "Михалыч", LocalDate.of(2022,2,18));
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            controller.update(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Пользователь с ID 0 не найден.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfCreateUserThatContains() throws ItemAlreadyExistException {
        User user3 = new User("User1@mail.ru", "LogUser1", "User#1", LocalDate.of(2020,12,31));
        Throwable thrown = assertThrows(ItemAlreadyExistException.class, () -> {
            controller.create(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Пользователь с электронной почтой User1@mail.ru уже зарегистрирован.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void getAllAndAddFriendTest() throws ValidationException {
        controller.addFriend(1L, 2L);
        Collection<User> friends = controller.getAllFriends(1L);
        Collection<User> friends2 = controller.getAllFriends(2L);
        assertEquals(1, friends.size(), "Неверное количество друзей.");
        assertEquals(0, friends2.size(), "Неверное количество друзей.");
    }

    @Test
    void removeFriendTest() throws ValidationException {
        controller.addFriend(1L, 2L);
        controller.addFriend(1L, 2L);
        controller.addFriend(1L, 2L);
        Collection<User> friends = controller.getAllFriends(1L);
        Collection<User> friends2 = controller.getAllFriends(2L);
        assertEquals(1, friends.size(), "Неверное количество друзей.");
        assertEquals(0, friends2.size(), "Неверное количество друзей.");
        controller.removeFriend(1L, 2L);
        Collection<User> friends4 = controller.getAllFriends(1L);
        assertEquals(0, friends4.size(), "Неверное количество друзей.");
    }

    @Test
    void shouldThrowExceptionIfNotFriends() throws ValidationException {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.removeFriend(2L, 3L);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Пользователи не были друзьями.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void getMutualFriendTest() throws ValidationException {
        controller.addFriend(1L, 2L);
        Collection<User> friends = controller.getAllFriends(1L);
        Collection<User> friends2 = controller.getAllFriends(2L);
        assertEquals(1, friends.size(), "Неверное количество друзей.");
        assertEquals(0, friends2.size(), "Неверное количество друзей.");
        controller.addFriend(3L, 2L);
        Collection<User> friends3 = controller.getAllFriends(3L);
        Collection<User> friends4 = controller.getAllFriends(2L);
        assertEquals(1, friends3.size(), "Неверное количество друзей.");
        assertEquals(0, friends4.size(), "Неверное количество друзей.");
        Collection<User> friends5 = controller.getMutualFriends(1L, 3L);
        assertEquals(1, friends5.size(), "Неверное количество друзей.");
    }
}