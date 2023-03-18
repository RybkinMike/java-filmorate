package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import javax.validation.Valid;
import java.util.Collection;

@Component
@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Запрошены все жанры");
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable("id") Long id) {
        log.info("Запрошен жанр по ID {}", id);
        return genreService.findById(id);
    }

    @PostMapping
    public Genre create(@RequestBody @Valid Genre genre) throws ValidationException {
        log.info("Добавлен жанр {}.", genre);
        return genreService.create(genre);
    }

    @PutMapping
    public Genre update(@RequestBody @Valid Genre genre) throws ValidationException {
        log.info("Данные жанра {} обновлены", genre);
        return genreService.update(genre);
    }
}