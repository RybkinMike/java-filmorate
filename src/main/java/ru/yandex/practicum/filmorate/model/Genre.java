package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Genre {
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;

}
