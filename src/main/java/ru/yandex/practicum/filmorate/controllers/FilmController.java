package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithLD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    //Группа содержащая все фильмы
    private Map<Integer, Film> films = new HashMap<>();
    //Функция выполняющая валидацию
    private Validation<Film> validationFields = (someFilm) -> {
        if (someFilm.getName().isBlank()) {
            throw new ValidationException("Ошибка. Название не заполнено.");
        }
        if (someFilm.getDescription().length() > 200) {
            throw new ValidationException("Ошибка. Длинна описания больше 200 символов.");
        }
        if (someFilm.convertToLocalDate((string) -> LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка. Дата релиза фильма не соответствует реальности.");
        }
        if (someFilm.getDuration() <= 0) {
            throw new ValidationException("Ошибка. Продолжительность фильма меньше или равна нулю.");
        }
        return true;
    };
    private static int idCount = 1;

    private int idGenerator() {
        return idCount++;
    }

    @PostMapping()
    public Film addFilm(@RequestBody Film film) {
        //Присвоить фильму идентификатор
        film = film.toBuilder().id(idGenerator()).build();

        //Добавление фильма в коллекцию если он прошёл валидацию иначе запись об ошибке в лог
        try {
            film.convertToLocalDate(string -> LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            film.validate(validationFields);
            films.put(film.getId(), film);
            log.info("В коллекцию добавлен фильм {}", film);
            return film;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        } catch (DateTimeParseException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Фильм {} не будет обновлён. Такого фильма нет в коллекции.", film);
            throw new ValidationException("Ошибка обновления фильма. Такого фильма нет в коллекции.");
        }
        //Добавление фильма в коллекцию если он прошёл валидацию иначе запись об ошибке в лог
        try {
            //Проверка правильности формата даты
            film.convertToLocalDate(string -> LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            //Проверка правильности заполнения полей объекта
            film.validate(validationFields);
            //Обновление объекта
            films.put(film.getId(), film);
            log.info("В коллекции обновлён фильм {}", film);
            return film;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        } catch (DateTimeParseException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    @GetMapping()
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }


    /////////////////////////////////////////// TODO: Убрать после исправления хранения даты
    @PostMapping("/film-ld")
    public FilmWithLD addFilmWithLocalDate(@RequestBody FilmWithLD film) {
        //Присвоить фильму идентификатор
//        film = film.toBuilder().id(idGenerator()).build();
        LocalDate date = film.getDate();
        String name = film.getName();
        return film;
        //Добавление фильма в коллекцию если он прошёл валидацию иначе запись об ошибке в лог
//        try {
////            film.convertToLocalDate(string -> LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
////            film.validate(validationFields);
//            films.put(film.getId(), film);
//            log.info("В коллекцию добавлен фильм {}", film);
//            return film;
//        } catch (ValidationException exception) {
//            log.warn(exception.getMessage(), exception);
//            throw exception;
//        } catch (DateTimeParseException exception) {
//            log.warn(exception.getMessage(), exception);
//            throw exception;
//        }
    }
}
