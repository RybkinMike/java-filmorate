package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class UserDbStorageImpl implements UserDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorageImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getUser(rs));
    }

    @Override
    public User getUser(ResultSet rs) throws SQLException {
        long id = rs.getInt("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("nickname");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        User user = new User(email, login, name, birthday);
        user.setId(id);
        return user;
    }

    @Override
    public Optional<User> findById(Long id){
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"user\" WHERE id = ?", id);
        if (userRows.next()) {
            User user = new User(
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("nickname"),
                    userRows.getDate("birthday").toLocalDate());
            log.info("Найден пользователь:Id = {}, Email = {}", id, user.getEmail());
            user.setId(id);
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public User create(final User user) {
        String sqlQuery = "INSERT INTO \"user\" (email, login, nickname, birthday) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        log.info("Пользователь {} зарегистрирован.", user.getEmail());
        return findByEmail(user.getEmail()).get();
    }

    @Override
    public User update(final User user) {
        String sqlQuery = "UPDATE \"user\" SET " +
                "email = ?, login = ?, nickname = ?, birthday = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Пользователь {} обновлен.", user.getEmail());
        return findByEmail(user.getEmail()).get();
    }

    @Override
    public boolean containsInFollowers(Long userId, Long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM friends " +
                "WHERE user_id = ? and friend_id = ?",userId , friendId);
        return userRows.next();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friends(user_id, friend_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery,
                friendId,
                userId);
    }

    @Override
    public void addFollower(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friends(user_id, friend_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery,
                userId ,
                friendId);
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        String sqlQueryDelete = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQueryDelete,
                userId,
                friendId) == 1;
    }

    @Override
    public Collection<User> getMutualFriends(Long userId, Long friendId) {
        String sql = "SELECT * FROM friends AS f1 INNER JOIN friends AS f2 ON f1.friend_id = f2.friend_id " +
                "INNER JOIN \"user\" AS u ON f2.friend_id = u.id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getUser(rs), userId, friendId);
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
        String sql = "SELECT * FROM \"user\" AS u INNER JOIN friends AS f ON u.id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getUser(rs), userId);
    }

    @Override
    public Optional<User> findByEmail(String email){
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"user\" WHERE email = ?", email);
        if (userRows.next()) {
            User user = new User(
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("nickname"),
                    userRows.getDate("birthday").toLocalDate());
            long id =  userRows.getInt("id");
            user.setId(id);
            log.info("Найден пользователь: Email = {}",  email);
            return Optional.of(user);
        } else {
            log.info("Пользователь с Email: {} не найден.", email);
            return Optional.empty();
        }
    }
}