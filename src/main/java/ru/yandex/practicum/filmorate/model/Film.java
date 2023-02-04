package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class Film extends Item {
    static final LocalDate LUMIERE = LocalDate.of(1895, 12, 28);
    @NotBlank(message = "Название не может быть пустым")
    private final String name;
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private Long duration;

    public Film(String name, String description, LocalDate releaseDate, Long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

}
