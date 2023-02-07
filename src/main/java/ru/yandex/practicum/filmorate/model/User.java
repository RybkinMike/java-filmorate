package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;

@Data
public class User extends Item {
    @Email(message = "email введен не верно")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    private final String login;
    private String name;
    @Past(message = "Приходи когда родишься")
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if (name == null || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }
}
