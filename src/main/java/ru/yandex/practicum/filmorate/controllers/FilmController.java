package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms().get();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film).get();
    }

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
     * Добавить лайк фильму от пользователя
     *
     * @param id
     * @param userId
     * @return
     */
    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable long id, @PathVariable long userId) {
        //TODO: handle NoSuchElementException
        return filmService.addLike(id, userId);
    }

    /**
     * Удалить лайк фульму от пользователя
     *
     * @param id
     * @param userId
     * @return
     */
    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable long id, @PathVariable long userId) {
        //TODO: handle NoSuchElementException
        return filmService.deleteLike(id, userId);
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
        //TODO: handle NoSuchElementException
        List<Film>  list = filmService.getBestFilms(count).get();
        return list;
    }
}
