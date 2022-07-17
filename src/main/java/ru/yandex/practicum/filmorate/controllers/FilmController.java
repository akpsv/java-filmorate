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

    /**
     * Получить фильм по идентификатору
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id){
        return filmService.getFilmById(id).get();
    }

    /**
     * Добавить лайк фильму от пользователя
     * @param id
     * @param userId
     * @return
     */
    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable long id, @PathVariable long userId){
        //TODO: handle NoSuchElementException
        Film film = filmService.getFilmById(id).get();
        Set<Long> likes = film.getLikes();
        if (likes.add(userId)) {
            film = film.toBuilder().likes(likes).build();
            return true;
        }
        return false;
    }

    /**
     * Удалить лайк фульму от пользователя
     * @param id
     * @param userId
     * @return
     */
    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable long id, @PathVariable long userId){
        //TODO: handle NoSuchElementException
        Film film = filmService.getFilmById(id).get();
        Set<Long> likes = film.getLikes();
        if (likes.remove(userId)){
            film = film.toBuilder().likes(likes).build();
            return true;
        }
        return false;
    }

    /**
     * Вернуть список из первых count фильмов по количеству лайков.
     * Если значение count не задано, вернуть первые 10 фильмов
     * @param count
     * @return
     */
    @GetMapping("/popular")
    public List<Film> getFilms(@RequestParam(defaultValue="10") int count){
        //TODO: handle NoSuchElementException
        return filmService.getBestFilms(count).get();
    }
}
