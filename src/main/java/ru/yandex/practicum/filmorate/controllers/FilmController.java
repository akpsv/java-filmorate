package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Получить фильмы
     *
     * @return
     */
    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms().get();
    }

    /**
     * Добавить фильм в группу
     *
     * @param film
     * @return - добавленный фильм
     */
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film).get();
    }

    /**
     * Обновить фильм в группе
     *
     * @param film
     * @return
     */
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film).get();
    }

    /**
     * Получить фильм по идентификатору
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id).get();
    }

    /**
     * Вернуть список из первых count фильмов по количеству лайков.
     * Если значение count не задано, вернуть первые 10 фильмов
     *
     * @param count
     * @return
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getBestFilms(count).get();
    }
}
