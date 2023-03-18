package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    private final FilmController controller;
    @Autowired private JdbcTemplate jdbcTemplate;
    @BeforeEach
    void beforeEach (){
        String sqlQuery1 = "INSERT INTO \"user\" (email, login, nickname, birthday) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery1,
                "User1@mail.ru",
                "LogUser1",
                "User#1",
                LocalDate.of(2020,12,31));
        String sqlQuery2 = "INSERT INTO \"user\" (email, login, nickname, birthday) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery2,
                "User2@mail.ru",
                "LogUser2",
                "User#2",
                LocalDate.of(2021,12,31));
        String sqlQuery3 = "INSERT INTO \"user\" (email, login, nickname, birthday) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery3,
                "User3@mail.ru",
                "LogUser3",
                "User#3",
                LocalDate.of(2022,12,31));

        String sqlQuery4 = "INSERT INTO film (film_name, description, releasedate, duration) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery4,
                "FilmName1",
                "FilmDesc1",
                LocalDate.of(2020,12,31),
                100L);
        String sqlQuery5 = "INSERT INTO film (film_name, description, releasedate, duration) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery5,
                "FilmName2",
                "FilmDesc2",
                LocalDate.of(2021,12,31),
                200L);
    }

    @Test
    void createAndFindAllTest() throws ValidationException {
        Collection<Film> films = controller.findAll();
        int n = films.size();
        Film film1 = new Film("FilmName3", "FilmDesc3", LocalDate.of(1987,4,18), 200L);
        controller.create(film1);
        Collection<Film> films1 = controller.findAll();
        assertNotNull(films1, "Контроллер пустой.");
        assertEquals(n+1, films1.size(), "Неверное количество итемов.");
    }

    @Test
    void update() throws ValidationException {


        Collection<Film> films = controller.findAll();
        int n = films.size();
        Film film1 = new Film("FilmName2", "UpdFilmDesc2", LocalDate.of(2022,4,18), 200L);
        film1.setId(2L);
        controller.update(film1);
        Collection<Film> films1 = controller.findAll();
        assertNotNull(films1, "Контроллер пустой.");
        assertEquals(n, films1.size(), "Неверное количество итемов.");
        Film film2 = controller.findFilm(2L);
        assertEquals(film2.getDescription(), film1.getDescription(), "Фильм не обновился.");



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
        assertEquals("Фильм с ID 0 не найден.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }

  /*  @Test
    void shouldTrowExceptionIfCreateFilmThatContains() throws ValidationException {
        Film film3 = new Film("FilmName2", "Продолжение нашумевшего хита про начинающего программиста. Учеба дается все труднее и труднее.", LocalDate.of(2023,1,6), 99L);
        Throwable thrown = assertThrows(ItemAlreadyExistException.class, () -> {
            controller.create(film3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Фильм с названием Новичок и Джава уже зарегистрирован.", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }
*/
    @Test
    void addAndRemoveLikeTest() throws ValidationException {

        controller.addLike(1L, 1L);
        Film film1 = controller.findFilm(1L);
        Set<Long> likes1 = film1.getLikes();
        assertNotNull(likes1, "Лайки пустые");
        assertEquals(likes1.size(), 1, "Лайки не соответствуют");
        assertTrue(likes1.contains(1L), "Лайки не соответствуют");
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addLike(1L, 1L);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("Данный пользователь уже поставил лайк", thrown.getMessage(), "Сообщение об ошибке не соответствует");
        assertNotNull(likes1, "Лайки не пустые");
        assertEquals(likes1.size(), 1, "Лайки не соответствуют");
        assertTrue(likes1.contains(1L), "Лайки не соответствуют");
        controller.removeLike(1L, 1L);
        Film film2 = controller.findFilm(1L);
        Set<Long> likes2 = film2.getLikes();
        assertEquals(likes2.size(), 0, "Лайки не соответствуют");

    }

    @Test
    void getPopularTest() throws ValidationException {
        controller.removeLike(1L, 1L);
        controller.removeLike(2L, 1L);
        controller.removeLike(3L, 1L);
        controller.removeLike(4L, 1L);
        controller.removeLike(1L, 2L);
        controller.removeLike(2L, 2L);
        controller.removeLike(3L, 2L);
        controller.removeLike(4L, 2L);
        controller.addLike(1L, 1L);
        controller.addLike(1L, 2L);
        controller.addLike(2L, 3L);
        List<Film> popular = controller.getPopular(2);
        assertNotNull(popular, "Популярные ильмы пустой");
        assertEquals(popular.size(), 2, "Количество фильмов не соответствуют");
        assertEquals(popular.get(0), controller.findFilm(1L), "Фильмы не соответствуют");
        assertEquals(popular.get(1), controller.findFilm(2L), "Фильмы не соответствуют");

    }

}