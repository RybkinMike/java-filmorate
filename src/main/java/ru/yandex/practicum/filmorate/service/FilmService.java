package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService extends AbstractService<Film> {
    Storage<User> userStorage;
    @Autowired
    public FilmService(Storage<Film> storage, Storage<User> userStorage) {
        this.userStorage = userStorage;
        this.storage = storage;
    }

    @Override
    public void validate(Film film) throws ValidationException {
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
    public void validateForPost(Film film) throws ValidationException, ItemAlreadyExistException {
        validate(film);
        Optional<Film> filmOpt = findByName(film.getName());
        if (!filmOpt.isEmpty()) {
            log.warn("Попытка внести уже зарегестрированный фильм");
            throw new ItemAlreadyExistException("Фильм с названием " + film.getName() + " уже зарегистрирован.");
        }
    }

    @Override
    public Long validateForPut(Film film) throws ValidationException, NotFoundException {
        validate(film);
        if (storage.findById(film.getId()) == null) {
            log.warn("Попытка отредактировать незарегестрированный фильм");
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден.");
        }
        return film.getId();
    }

    public void addLike (Long filmId, Long userId) throws ValidationException {
        Film film = storage.findById(filmId);
        User user = userStorage.findById(userId);
        if (!user.isLike()) {
            Set<Long> tempLikes = new HashSet<>();
            if (film.getLikes() != null) {
                tempLikes = film.getLikes();
            }
            tempLikes.add(user.getId());
            film.setLikes(tempLikes);
            user.setLike(true);
        }
        else {
            throw new ValidationException("Данный пользователь уже поставил лайк");
        }
    }

    public void removeLike (Long filmId, Long userId) throws ValidationException {
        Film film = storage.findById(filmId);
        User user = userStorage.findById(userId);
        if (user.isLike() && film.getLikes().contains(user.getId())) {
            Set<Long> tempLikes = film.getLikes();
            tempLikes.remove(user.getId());
            film.setLikes(tempLikes);
            user.setLike(false);
        }
        else {
            throw new ValidationException("Данный пользователь еще ничего не лайкнул");
        }
    }

    public List<Film> getPopular (Integer count){
        return storage.findAll().stream()
                .sorted(COMPARATOR).
                limit(count).
                collect(Collectors.toList());
    }
    public static final Comparator<Film> COMPARATOR = Comparator.comparingLong(Film::getRate).reversed();

    public Optional<Film> findByName (String filmName) {
        Film film = null;
        if (storage != null) {
            for (Film filmInFilms : storage.findAll()) {
                if (filmInFilms.getName().equals(filmName)) {
                    film = filmInFilms;
                }
                else {
                    return Optional.empty();
                }
            }
        }
        else {
            return Optional.empty();
        }
        return Optional.ofNullable(film);
    }
}
