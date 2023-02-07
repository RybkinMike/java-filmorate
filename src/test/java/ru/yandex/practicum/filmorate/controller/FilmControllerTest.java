package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static Controller controller;
    Film film1;
    Film film2;

    @BeforeEach
    public void beforeEach() throws ValidationException {
        controller = new FilmController();
        film1 = new Film("Новичок и Джава", "Начинающий программист осваивает джаву. Он записывается на курсы.", LocalDate.of(2022,8,15), 98L);
        controller.create(film1);
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
        filmUpdate.setId(1);
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
    void shouldTrowExceptionIfUpdateFilmThatNotContains() throws ValidationException {
        Film film3 = new Film("Продолжение 2", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 99L);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.update(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Фильм с ID 0 не найден.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

    @Test
    void shouldTrowExceptionIfCreateFilmThatContains() throws ValidationException {
        Film film3 = new Film("Новичок и Джава", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 99L);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.create(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Фильм с названием Новичок и Джава уже зарегистрирован.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }
}