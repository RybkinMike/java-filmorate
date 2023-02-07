package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController extends Controller<Film> {

    @Override
    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на все фильмы");
        return super.findAll();
    }

    @Override
    @PostMapping
    public Film create(@RequestBody @Valid @NotBlank Film film) throws ValidationException {
        log.info("Добавлен фильм {}", film);
        return super.create(film);
    }

    @Override
    @PutMapping
    public Film update(@RequestBody @Valid @NotBlank Film film) throws ValidationException {
        log.info("Данные фильма {} обновлены", film);
        return super.update(film);
    }

    @Override
    void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Указано пустое название");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Указано слишком длинное описание");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(Film.FIRST_FILM)) {
            log.warn("Указана неверная дата релиза");
            throw new ValidationException("Прости, но братья Люмьер были первыми");
        }
        if (film.getDuration() < 1) {
            log.warn("Указана неверная продолжительность");
            throw new ValidationException("Ну хоть одну секундочку должен фильм идти");
        }
    }

    @Override
    void validateForPost(Film film) throws ValidationException {
        validate(film);
        for (Film filmInFilms : items.values()) {
            if (filmInFilms.getName().equals(film.getName())) {
                log.warn("Попытка внести уже зарегестрированный фильм");
                throw new ValidationException("Фильм с названием " + film.getName() + " уже зарегистрирован.");
            }
        }
    }

    @Override
    Long validateForPut(Film film) throws ValidationException {
        validate(film);
        if (!items.containsKey(film.getId())) {
            log.warn("Попытка отредактировать незарегестрированного пользователя");
            throw new ValidationException("Фильм с ID " + film.getId() + " не найден.");
        }
        return film.getId();
    }
}
