package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static FilmController controller;
    Film film1;
    Film film2;
    Storage<Film> storage = new InMemoryFilmStorage();
    Storage<User> userStorage = new InMemoryUserStorage();
    FilmService filmService = new FilmService(storage, userStorage);


    @BeforeEach
    public void beforeEach() throws ValidationException {
        controller = new FilmController(filmService);
        film1 = new Film("Новичок и Джава", "Начинающий программист осваивает джаву. Он записывается на курсы.", LocalDate.of(2022,8,15), 98L);
        Long id = film1.getId();
        controller.create(film1);
        Long id2 = film1.getId();
        film2 = new Film("Джава побеждает", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее.", LocalDate.of(2023,1,5), 92L);
    }

    @Test
    void findAllTest() {
        Collection<Film> films = controller.findAll();
        assertNotNull(films, "Контроллер пустой.");
        assertEquals(1, films.size(), "Неверное количество итемов.");
    }

    @Test
    void create() throws ValidationException {
        Collection<Film> films = controller.findAll();
        assertEquals(1, films.size(), "Неверное количество фильмов.");
        controller.create(film2);
        Collection<Film> films2 = controller.findAll();
        assertEquals(2, films.size(), "Неверное количество фильмов.");
        assertTrue(films2.contains(film1), "Фильм1 отсутствует.");
        assertTrue(films2.contains(film2), "Фильм2 отсутствует.");
    }

    @Test
    void update() throws ValidationException {
        Collection<Film> films = controller.findAll();
        assertEquals(1, films.size(), "Неверное количество фильмов.");
        Film filmUpdate = new Film("Новичок и Джава", "Начинающий программист осваивает джаву. Он записывается на курсы. Курсы в ЯндексПрактикум.", LocalDate.of(2022,8,15), 98L);
        filmUpdate.setId(film1.getId());
        Long id = filmUpdate.getId();
        controller.update(filmUpdate);
        Collection<Film> films2 = controller.findAll();
        assertEquals(1, films2.size(), "Неверное количество фильмов.");
        assertTrue(films2.contains(filmUpdate), "Фильм1 не обновился.");
    }

    @Test
    void shouldTrowExceptionIfNameIsBlank() throws ValidationException {
        Film film3 = new Film(" ", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 90L);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Название фильма не может быть пустым.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfDescriptionIsLonger200() throws ValidationException {
        String description = "A";
        for (int i = 0; i < 200; i++) {
            description = description + "A";
        }
        Film film3 = new Film("Продолжение", description, LocalDate.of(2023,1,6), 90L);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Максимальная длина описания — 200 символов", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfReleaseIsBeforeLumiere() throws ValidationException {
        Film film3 = new Film("Продолжение", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(1895,12,27), 90L);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Прости, но братья Люмьер были первыми", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfDurationIsBelowZero() throws ValidationException {
        Film film3 = new Film("Продолжение", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 0L);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Ну хоть одну секундочку должен фильм идти", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfUpdateFilmThatNotContains() throws NotFoundException {
        Film film3 = new Film("Продолжение 2", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 99L);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            controller.update(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Данные по 0 не найден.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfCreateFilmThatContains() throws ValidationException {
        Film film3 = new Film("Новичок и Джава", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 99L);
        Throwable thrown = assertThrows(ItemAlreadyExistException.class, () -> {
            controller.create(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Фильм с названием Новичок и Джава уже зарегистрирован.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void addLikeTest() throws ValidationException {
        Set<Long> likes = film1.getLikes();
        assertNull(likes, "Лайки не пустые");
        //assertEquals("Название фильма не может быть пустым.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
        User user1 = new User("ya@yandex.ru", "Начинающий программист", "Михаил", LocalDate.of(1987,4,18));
        userStorage.create(user1);
        controller.addLike(film1.getId(), user1.getId());
        Set<Long> likes1 = film1.getLikes();
        assertNotNull(likes1, "Лайки не пустые");
        assertEquals(likes1.size(), 1, "Лайки не соответствуют");
        assertTrue(likes1.contains(user1.getId()), "Лайки не соответствуют");
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addLike(film1.getId(), user1.getId());
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Данный пользователь уже поставил лайк", thrown.getMessage(), "Сообщение об ошибке не соответствует");
        assertNotNull(likes1, "Лайки не пустые");
        assertEquals(likes1.size(), 1, "Лайки не соответствуют");
        assertTrue(likes1.contains(user1.getId()), "Лайки не соответствуют");
    }

    @Test
    void removeLikeTest() throws ValidationException {
        Set<Long> likes = film1.getLikes();
        assertNull(likes, "Лайки не пустые");
        //assertEquals("Название фильма не может быть пустым.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
        User user1 = new User("ya@yandex.ru", "Начинающий программист", "Михаил", LocalDate.of(1987,4,18));
        User user2 = new User("ya@mail.ru", "Сеньор-помидор", "Артем", LocalDate.of(1999,1,12));
        userStorage.create(user1);
        userStorage.create(user2);
        controller.addLike(film1.getId(), user1.getId());
        controller.addLike(film1.getId(), user2.getId());
        Set<Long> likes1 = film1.getLikes();
        assertNotNull(likes1, "Лайки не пустые");
        assertEquals(likes1.size(), 2, "Лайки не соответствуют");
        assertTrue(likes1.contains(user1.getId()), "Лайки не соответствуют");
        assertTrue(likes1.contains(user2.getId()), "Лайки не соответствуют");
        controller.removeLike(film1.getId(), user1.getId());
        Set<Long> likes2 = film1.getLikes();
        assertNotNull(likes2, "Лайки не пустые");
        assertEquals(likes2.size(), 1, "Лайки не соответствуют");
        assertTrue(likes2.contains(user2.getId()), "Лайки не соответствуют");
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.removeLike(film1.getId(), user1.getId());
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Данный пользователь еще ничего не лайкнул", thrown.getMessage(), "Сообщение об ошибке не соответствует");
        assertNotNull(likes1, "Лайки не пустые");
        assertEquals(likes1.size(), 1, "Лайки не соответствуют");
        assertTrue(likes1.contains(user2.getId()), "Лайки не соответствуют");
        controller.addLike(film1.getId(), user1.getId());
        assertEquals(likes1.size(), 2, "Лайки не соответствуют");
        assertTrue(likes1.contains(user1.getId()), "Лайки не соответствуют");
    }

    @Test
    void getPopularTest() throws ValidationException {
        film2.setLikes(Set.of(11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L));
        controller.create(film2);
        Film film3 = new Film("Фильм3", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 90L);
        film3.setLikes(Set.of(21L, 22L, 23L, 24L, 25L, 26L, 27L));
        controller.create(film3);
        Film film4 = new Film("Фильм4", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 90L);
        film4.setLikes(Set.of(31L, 32L));
        controller.create(film4);
        Film film5 = new Film("Фильм5", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 90L);
        film5.setLikes(Set.of(41L, 42L, 43L, 44L, 45L));
        controller.create(film5);
        List<Film> popular = controller.getPopular(3);
        assertNotNull(popular, "Популярные ильмы пустой");
        assertEquals(popular.size(), 3, "Количество фильмов не соответствуют");
        assertEquals(popular.get(0), film2, "Фильмы не соответствуют");
        assertEquals(popular.get(1), film3, "Фильмы не соответствуют");
        assertEquals(popular.get(2), film5, "Фильмы не соответствуют");

    }

}