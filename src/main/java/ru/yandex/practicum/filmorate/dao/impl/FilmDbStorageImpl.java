package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class FilmDbStorageImpl implements FilmDbStorage {
    private final JdbcTemplate jdbcTemplate;
    GenreDbStorageImpl genreDb;
    MpaDbStorageImpl mpaDb;
    public FilmDbStorageImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
        this.genreDb = new GenreDbStorageImpl(jdbcTemplate);
        this.mpaDb = new MpaDbStorageImpl(jdbcTemplate);
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM film AS f LEFT OUTER JOIN film_mpa AS fm ON f.id = fm.film_id " +
                "LEFT OUTER JOIN mpa AS m ON fm.mpa_id=m.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilm(rs));
    }

    private Film getFilm(ResultSet rs) throws SQLException {
        long id = rs.getInt("id");
        long mpaId = rs.getInt("mpa_id");
        String nameMpa = rs.getString("mpa_name");
        String name = rs.getString("film_name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("releasedate").toLocalDate();
        Long duration = (long) rs.getInt("duration");
        List <Genre> genres = genreDb.getGenresByFilmId(id);
        Mpa mpa = new Mpa(mpaId, nameMpa);
        Film film = new Film(name, description, releaseDate, duration, genres, mpa);
        film.setId(id);
        return film;
    }

    @Override
    public Optional<Film> findById(Long id){
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM film AS f " +
                "LEFT OUTER JOIN film_mpa AS fm ON f.id = fm.film_id " +
                "LEFT OUTER JOIN mpa AS m ON mpa_id=m.id WHERE f.id = ?", id);
        if(filmRows.next()) {
            List <Genre> genres = genreDb.getGenresByFilmId(id);
            long mpaId = filmRows.getInt("mpa_id");
            String nameMpa = filmRows.getString("mpa_name");
            Mpa mpa = new Mpa(mpaId, nameMpa);
            Film film = new Film(
                    filmRows.getString("film_name"),
                    filmRows.getString("description"),
                    filmRows.getDate("releasedate").toLocalDate(),
                    (long) filmRows.getInt("duration"),
                    genres,
                    mpa);
            film.setId(id);
            film.setLikes(
                    getLikesById(id)
            );
            log.info("Найден фильм: название = {}",  film);
            return Optional.of(film);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Film create(final Film film) {
        String sqlQuery = "INSERT INTO film(film_name, description, releasedate, duration) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());
        Long id = findIdByName(film.getName()).get();
        film.setId(id);
        if (film.getGenres() != null) {
            int i = 0;
            while (i < film.getGenres().size()) {
                String sqlQueryGenre = "INSERT INTO film_genre(film_id, genre_id) " +
                        "VALUES (?, ?)";
                jdbcTemplate.update(sqlQueryGenre,
                        id,
                        film.getGenres().get(i).getId());
                i++;
            }
        }
        if (film.getMpa() != null) {
            String sqlQueryMpa = "INSERT INTO film_mpa(film_id, mpa_id) " +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlQueryMpa,
                    id,
                    film.getMpa().getId());
        }
        log.info("Фильм {} зарегистрирован.", film.getName());
        return film;
    }

    @Override
    public Film update(final Film film) {
        String sqlQuery = "UPDATE film SET " +
                "film_name = ?, description = ?, releasedate = ?, duration = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());
        Long id = findIdByName(film.getName()).get();
        film.setId(id);
        String sqlQueryDelete = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryDelete,
                id);
        if (film.getGenres() != null) {
            int i = 0;
            while (i < film.getGenres().size()) {
                String sqlQueryGenre = "INSERT INTO film_genre(film_id, genre_id) " +
                        "SELECT ?, ? FROM DUAL " +
                        "WHERE NOT EXISTS (SELECT * FROM film_genre " +
                        "WHERE film_id = ? AND genre_id = ? LIMIT 1)";
                jdbcTemplate.update(sqlQueryGenre,
                        id,
                        film.getGenres().get(i).getId(),
                        id,
                        film.getGenres().get(i).getId());
                i++;
            }
        }
        if (film.getMpa() != null) {
            String sqlQueryMpa = "UPDATE film_mpa SET " +
                    "film_id = ?, mpa_id = ? " +
                    "WHERE film_id = ?";
            jdbcTemplate.update(sqlQueryMpa,
                    id,
                    film.getMpa().getId(),
                    id);
        }
        else {
            String sqlQueryDeleteMpa = "DELETE FROM film_mpa WHERE film_id = ?";
            jdbcTemplate.update(sqlQueryDeleteMpa,
                    id);
        }
        log.info("Фильм {} бновлен.", film.getName());
        return findById(id).get();
    }

    @Override
    public Optional <Long> findIdByName(String filmName) {
        SqlRowSet filmNameRow = jdbcTemplate.queryForRowSet("SELECT id FROM film WHERE film_name = ?", filmName);
        if (filmNameRow.next()) {
            Long id = (long) filmNameRow.getInt("id");
            log.info("Найден фильм:Id = {}, название = {}", id, filmName);
            return Optional.of(id);
        } else {
            log.info("Фильм с названием {} не найден.", filmName);
            return Optional.empty();
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) throws ValidationException {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM likes WHERE user_id = ?", userId);
        if (userRows.next()) {
            throw new ValidationException("Данный пользователь уже поставил лайк");
        } else {
            String sqlQuery = "INSERT INTO likes(film_id, user_id) " +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    userId);
            log.info("Пользователь {} поставил лайк фильму {}.", userId, filmId);
        }
    }

    @Override
    public void removeLike(Long userId) throws NotFoundException {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"user\" WHERE id = ?", userId);
        if (!userRows.next()) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден.");
        }
        String sqlQueryDelete = "DELETE FROM likes WHERE user_id = ?";
        jdbcTemplate.update(sqlQueryDelete,
                userId);
    }

    @Override
    public Set<Long> getLikesById(Long filmId) {
        Set<Long> likes = new HashSet<>();
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("SELECT * FROM likes WHERE film_id = ?", filmId);
        while (likesRows.next()) {
            likes.add((long) likesRows.getInt("user_id"));
        }
        return likes;
    }

    @Override
    public List<Film> getPopular(Integer count){
        List<Film> popularFilms = new ArrayList<>();
        String sql = "SELECT id, COUNT(user_id) FROM film AS f LEFT OUTER JOIN likes AS l ON f.id = l.film_id " +
                "GROUP BY f.id ORDER BY COUNT(user_id) DESC LIMIT ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, count);
        while (filmRows.next()) {
            long id = filmRows.getInt("id");
            popularFilms.add(findById(id).get());
        }
        return popularFilms;
    }
}