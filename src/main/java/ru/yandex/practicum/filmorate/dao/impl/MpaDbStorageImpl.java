package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class MpaDbStorageImpl implements MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getMpa(rs));
    }

    @Override
    public Mpa getMpa(ResultSet rs) throws SQLException {
        long id = rs.getInt("id");
        String name = rs.getString("mpa_name");
        return new Mpa(id, name);
    }

    @Override
    public Optional<Mpa> findById(long id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    id,
                    mpaRows.getString("mpa_name"));
            log.info("Найден рейтинг:Id = {}, название = {}", id, mpa.getName());
            return Optional.of(mpa);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Mpa create(final Mpa mpa) {
        String sqlQuery = "INSERT INTO mpa(mpa_name) " +
                "VALUES (?)";
        jdbcTemplate.update(sqlQuery,
                mpa.getName());
        log.info("Рейтинг {} зарегистрирован.", mpa.getName());
        return mpa;
    }

    @Override
    public Mpa update(final Mpa mpa) {
        String sqlQuery = "UPDATE mpa SET " +
                "mpa_name = ?";
        jdbcTemplate.update(sqlQuery,
                mpa.getName());
        log.info("Рейтинг {} бновлен.", mpa.getName());
        return mpa;
    }
}