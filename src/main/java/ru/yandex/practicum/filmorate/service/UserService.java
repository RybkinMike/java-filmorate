package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService extends AbstractService<User> {

    @Autowired
    public UserService(Storage<User> storage) {
        this.storage = storage;
    }

    @Override
    public void validate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Указан пустой Email");
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            log.warn("Указан неверный Email");
            throw new ValidationException("Адрес электронной почты имеет неправильный формат.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Указан пустой логин");
            throw new ValidationException("Логин не может быть пустым.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Указана неверная дата рождения");
            throw new ValidationException("Приходи, после того как родишься.");
        }
    }

    @Override
    public void validateForPost(User user) throws ItemAlreadyExistException, ValidationException {
        validate(user);
        Optional<User> userOpt = findByEmail(user.getEmail());
        if (!userOpt.isEmpty()) {
            log.warn("Попытка внести уже зарегестрированного пользователя");
            throw new ItemAlreadyExistException("Пользователь с электронной почтой " + user.getEmail() + " уже зарегистрирован.");
        }
    }

    @Override
    public Long validateForPut(User user) throws NotFoundException, ValidationException {
        validate(user);
        if (storage.findById(user.getId()) == null) {
            log.warn("Попытка отредактировать незарегестрированного пользователя");
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден.");
        }
        return user.getId();
    }

    public void addFriend (Long userId, Long friendId) throws ValidationException {
        User user = findById(userId);
        User friend = findById(friendId);
        if (user.getFriends() == null || friend.getFriends() == null || !user.getFriends().contains(friend.getId())) {
            Set<Long> tempFriends = new HashSet<>();
            Set<Long> tempFriends2 = new HashSet<>();
            if (user.getFriends() != null) {
                tempFriends = user.getFriends();
            }
            tempFriends.add(friendId);
            user.setFriends(tempFriends);
            if (friend.getFriends() != null) {
                tempFriends2 = friend.getFriends();
            }
            tempFriends2.add(userId);
            friend.setFriends(tempFriends2);
        }
        else {
            throw new ValidationException("Пользователи уже дружат.");
        }
    }

    public void removeFriend (Long userId, Long friendId) throws ValidationException {
        User user = findById(userId);
        User friend = findById(friendId);
        if (user.getFriends() != null && friend.getFriends() != null && user.getFriends().contains(friend.getId())) {
            Set<Long> tempFriends = user.getFriends();
            tempFriends.remove(friend.getId());
            user.setFriends(tempFriends);
            tempFriends = friend.getFriends();
            tempFriends.remove(user.getId());
            friend.setFriends(tempFriends);
        }
        else {
            throw new ValidationException("Пользователи не были друзьями.");
        }
    }

    public Collection<User> getMutualFriends (Long userId, Long friendId) {
        Collection<User> mutualFriends = new HashSet<>();
        Collection<Long> mutualFriendsId = new HashSet<>();
        User user = findById(userId);
        User friend = findById(friendId);
        if (user.getFriends() != null && friend.getFriends() != null) {
            Collection<Long> userFriends = user.getFriends();
            for (Long id : userFriends) {
                mutualFriendsId.add(id);
            }
            Collection<Long> retainFriend = friend.getFriends();
            mutualFriendsId.retainAll(retainFriend);
            for (Long id : mutualFriendsId) {
                mutualFriends.add(findById(id));
            }
        }
        return mutualFriends;
    }

    public Collection<User> getAllFriends (Long userId) {
        User user = findById(userId);
        Set<User> friends = new HashSet<>();
        Set<Long> friendsId = user.getFriends();
        if (friendsId != null) {
            for (Long id : friendsId) {
                friends.add(findById(id));
            }
        }
        return friends.stream().sorted(COMPARATOR).collect(Collectors.toList());
    }
    public static final Comparator<User> COMPARATOR = Comparator.comparingLong(User::getId);

    public Optional<User> findByEmail (String userEmail) {
        User user = null;
        if (storage != null) {
            for (User userInUsers : storage.findAll()) {
                if (userInUsers.getEmail().equals(userEmail)) {
                    user = userInUsers;
                }
                else {
                    return Optional.empty();
                }
            }
        }
        else {
            return Optional.empty();
        }
        return Optional.ofNullable(user);
    }
}
