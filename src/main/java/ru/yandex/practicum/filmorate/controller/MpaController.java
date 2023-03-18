package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import javax.validation.Valid;
import java.util.Collection;

@Component
@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Запрошены все рейтенги");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findMpa(@PathVariable("id") Long id) {
        log.info("Запрошен рейтинг по ID {}", id);
        return mpaService.findById(id);
    }

    @PostMapping
    public Mpa create(@RequestBody @Valid Mpa mpa) throws ValidationException {
        log.info("Добавлен рейтинг {}.", mpa);
        return mpaService.create(mpa);
    }

    @PutMapping
    public Mpa update(@RequestBody @Valid Mpa mpa) throws ValidationException {
        log.info("Данные рейтинга {} обновлены", mpa);
        return mpaService.update(mpa);
    }
}