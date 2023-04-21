package ru.yandex.practicum.filmorate.model;
import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Data
@AllArgsConstructor
public class Mpa {
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;


}
