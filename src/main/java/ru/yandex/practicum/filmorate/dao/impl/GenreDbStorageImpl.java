package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class GenreDbStorageImpl implements GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getGenre(rs));
    }

    @Override
    public Genre getGenre(ResultSet rs) throws SQLException {
        long id = rs.getInt("genre_id");
        String name = rs.getString("genre_name");
        return new Genre(id, name);
    }

    @Override
    public Optional<Genre> findById(long id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre WHERE genre_id = ?", id);
        if (genreRows.next()) {
            Genre genre = new Genre(
                    id,
                    genreRows.getString("genre_name"));
            log.info("Найден жанр:Id = {}, название = {}", id, genre.getName());
            return Optional.of(genre);
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Genre create(final Genre genre) {
        String sqlQuery = "INSERT INTO genre(genre_name) " +
                "VALUES (?)";
        jdbcTemplate.update(sqlQuery,
                genre.getName());
        log.info("Жанр {} зарегистрирован.", genre.getName());
        return genre;
    }

    @Override
    public Genre update(final Genre genre) {
        String sqlQuery = "UPDATE genre SET " +
                "genre_name = ?";
        jdbcTemplate.update(sqlQuery,
                genre.getName());
        log.info("Жанр {} бновлен.", genre.getName());
        return genre;
    }

    @Override
    public List<Genre> getGenresByFilmId(long id) {
        String sql = "SELECT * FROM film AS f  JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id WHERE f.id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getGenre(rs), id);
    }
}