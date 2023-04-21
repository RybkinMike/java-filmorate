package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTest {
    private final GenreController controller;

    @Test
    void findAllTest() {
        Collection<Genre> genres = controller.findAll();
        assertNotNull(genres, "Контроллер пустой.");
        assertEquals(6, genres.size(), "Неверное количество итемов.");
    }

    @Test
    void findByIdTest() {
        Genre genre1 = controller.findGenre(1L);
        assertNotNull(genre1, "Контроллер пустой.");
        assertEquals(genre1.getName(), "Комедия", "Не тот жанр.");
    }

}
