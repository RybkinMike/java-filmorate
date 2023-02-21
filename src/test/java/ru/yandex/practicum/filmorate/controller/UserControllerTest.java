package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class UserControllerTest {
    private static UserController controller;
    User user1;
    User user2;

    Storage<User> storage = new InMemoryUserStorage();
    UserService userService = new UserService(storage);

    @BeforeEach
    public void beforeEach() throws ValidationException {
        controller = new UserController(userService);
        user1 = new User("ya@yandex.ru", "Начинающий программист", "Михаил", LocalDate.of(1987,4,18));
        controller.create(user1);
        user2 = new User("ya@mail.ru", "Сеньор-помидор", "Артем", LocalDate.of(1999,1,12));
    }

    @Test
    void findAllTest() {
        Collection<User> users = controller.findAll();
        assertNotNull(users, "Контроллер пустой.");
        assertEquals(1, users.size(), "Неверное количество итемов.");
    }

    @Test
    void findByIdTest() {
        User user = controller.findUser(user1.getId());
        assertEquals(user, user1, "Пользователи не совпали.");
    }

    @Test
    void create() throws ValidationException {
        Collection<User> users = controller.findAll();
        assertEquals(1, users.size(), "Неверное количество пользоватеоей.");
        controller.create(user2);
        Collection<User> users2 = controller.findAll();
        assertEquals(2, users2.size(), "Неверное количество фильмов.");
        assertTrue(users2.contains(user1), "Пользователь1 отсутствует.");
        assertTrue(users2.contains(user2), "Пользователь2 отсутствует.");
    }

    @Test
    void update() throws ValidationException {
        Collection<User> users = controller.findAll();
        assertEquals(1, users.size(), "Неверное количество пользователей.");
        User userUpdate = new User("ya@yandex.ru", "Начинающий программист", "Михалыч", LocalDate.of(1987,4,18));
        userUpdate.setId(1);
        controller.update(userUpdate);
        Collection<User> users2 = controller.findAll();
        System.out.println(users2);
        assertEquals(1, users2.size(), "Неверное количество пользователей.");
        assertTrue(users2.contains(userUpdate), "Пользователь1 не обновился.");
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
        assertEquals("Данные по 0 не найден.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfCreateUserThatContains() throws ItemAlreadyExistException {
        User user3 = new User("ya@yandex.ru", "111", "Михалыч", LocalDate.of(2022,2,18));
        Throwable thrown = assertThrows(ItemAlreadyExistException.class, () -> {
            controller.create(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Пользователь с электронной почтой ya@yandex.ru уже зарегистрирован.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void getAllAndAddFriendTest() throws ValidationException {
        controller.create(user2);
        controller.getAllFriends(user1.getId());
        controller.addFriend(user1.getId(), user2.getId());
        Collection<User> friends = controller.getAllFriends(user1.getId());
        Collection<User> friends2 = controller.getAllFriends(user2.getId());
        assertEquals(1, friends.size(), "Неверное количество друзей.");
        assertTrue(friends.contains(user2), "Друг отсутствует.");
        assertEquals(1, friends2.size(), "Неверное количество друзей.");
        assertTrue(friends2.contains(user1), "Друг отсутствует.");
    }

    @Test
    void removeFriendTest() throws ValidationException {
        controller.create(user2);
        controller.addFriend(user1.getId(), user2.getId());
        Collection<User> friends = controller.getAllFriends(user1.getId());
        Collection<User> friends2 = controller.getAllFriends(user2.getId());
        assertEquals(1, friends.size(), "Неверное количество друзей.");
        assertTrue(friends.contains(user2), "Друг отсутствует.");
        assertEquals(1, friends2.size(), "Неверное количество друзей.");
        assertTrue(friends2.contains(user1), "Друг отсутствует.");
        User user3 = new User("1@1.ru", "111", " ", LocalDate.of(2023,2,18));
        controller.create(user3);
        controller.addFriend(user1.getId(), user3.getId());
        Collection<User> friends3 = controller.getAllFriends(user1.getId());
        assertEquals(2, friends3.size(), "Неверное количество друзей.");
        assertTrue(friends3.contains(user3), "Друг отсутствует.");
        controller.removeFriend(user1.getId(), user2.getId());
        Collection<User> friends4 = controller.getAllFriends(user1.getId());
        assertEquals(1, friends4.size(), "Неверное количество друзей.");
        assertTrue(friends4.contains(user3), "Друг отсутствует.");
        assertFalse(friends4.contains(user2), "Друг присутствует.");
        Collection<User> friends5 = controller.getAllFriends(user2.getId());
        assertEquals(0, friends5.size(), "Неверное количество друзей.");
    }
    @Test
    void shouldThrowExceptionIfNotFriends() throws ValidationException {
        controller.create(user2);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.removeFriend(user1.getId(), user2.getId());
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Пользователи не были друзьями.", thrown.getMessage(), "Сообщение об ошибке не соответствует");

    }

    @Test
    void getMutualFriendTest() throws ValidationException {
        controller.create(user2);
        User user3 = new User("1@1.ru", "111", " ", LocalDate.of(2023,2,18));
        controller.create(user3);
        Collection<User> friends1 = controller.getMutualFriends(user1.getId(), user2.getId());
        assertEquals(0, friends1.size(), "Неверное количество друзей.");
        controller.addFriend(user1.getId(), user3.getId());
        controller.addFriend(user2.getId(), user3.getId());
        Collection<User> friends3 = controller.getAllFriends(user1.getId());
        assertEquals(1, friends3.size(), "Неверное количество друзей.");
        Collection<User> friends = controller.getMutualFriends(user1.getId(), user2.getId());
        assertEquals(1, friends.size(), "Неверное количество друзей.");
        assertTrue(friends.contains(user3), "Друг отсутствует.");
        Collection<User> friends2 = controller.getMutualFriends(user1.getId(), user3.getId());
        assertEquals(0, friends2.size(), "Неверное количество друзей.");
        Collection<User> friends4 = controller.getAllFriends(user1.getId());
        assertEquals(1, friends4.size(), "Неверное количество друзей.");
        User user4 = new User("11@1.ru", "1111", " ", LocalDate.of(2023,2,18));
        controller.create(user4);
        Collection<User> friends5 = controller.getMutualFriends(user1.getId(), user4.getId());
        Collection<User> friends6 = controller.getAllFriends(user1.getId());
        assertEquals(1, friends6.size(), "Неверное количество друзей.");

    }
}