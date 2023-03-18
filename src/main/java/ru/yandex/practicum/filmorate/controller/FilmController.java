package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Component
@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрошены все фильмы");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable("id") Long id){
        log.info("Запрошен фильм по ID {}", id);
        return filmService.findById(id);
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) throws ValidationException {
        log.info("Добавлен фильм {}.", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) throws ValidationException {
        log.info("Данные фильма {} обновлены", film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@Valid @PathVariable Long id, @PathVariable Long userId) throws ValidationException {
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@Valid @PathVariable Long id, @PathVariable Long userId) throws ValidationException {
        log.info("Пользователь с ID {} удалил лайк фильму с ID {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Запрос на {} самых популярных фильмов", count);
        return filmService.getPopular(count);
    }

}
