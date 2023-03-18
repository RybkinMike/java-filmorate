package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface UserDbStorage {
    public Collection<User> findAll();

    User getUser(ResultSet rs) throws SQLException;
    public Optional<User> findById(Long id);
    public User create(User user) throws ValidationException;
    public User update(User user) throws ValidationException;
    boolean containsInFollowers(Long userId, Long friendId);
    void addFriend(Long userId, Long friendId);
    void addFollower(Long userId, Long friendId);
    boolean removeFriend(Long userId, Long friendId);
    Collection<User> getMutualFriends(Long userId, Long friendId);
    Collection<User> getAllFriends(Long userId);
    Optional<User> findByEmail(String email);
}
