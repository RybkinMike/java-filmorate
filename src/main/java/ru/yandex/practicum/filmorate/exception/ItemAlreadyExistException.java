package ru.yandex.practicum.filmorate.exception;

public class ItemAlreadyExistException extends RuntimeException {
    public ItemAlreadyExistException(String message) {
        super(message);
    }
}
