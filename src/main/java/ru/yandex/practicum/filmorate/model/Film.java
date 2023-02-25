package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Film extends Item {
    public static final LocalDate FIRST_FILM = LocalDate.of(1895, 12, 28);
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальный размер описания - 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private Long duration;
    private Set<Long> likes;

    public Film(String name, String description, LocalDate releaseDate, Long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }


    public Long getRate () {
        if (likes == null) {
            return 0L;
        }
        return (long)likes.size();
    }
}
