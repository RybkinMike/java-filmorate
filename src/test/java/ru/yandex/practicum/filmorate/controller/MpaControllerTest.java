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
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaControllerTest {
    private final MpaController controller;

    @Test
    void findAllTest() {
        Collection<Mpa> mpas = controller.findAll();
        assertNotNull(mpas, "Контроллер пустой.");
        assertEquals(5, mpas.size(), "Неверное количество итемов.");
    }

    @Test
    void findByIdTest() {
        Mpa mpa1 = controller.findMpa(1L);
        assertNotNull(mpa1, "Контроллер пустой.");
        assertEquals(mpa1.getName(), "G", "Не тот MPA.");
    }

}
