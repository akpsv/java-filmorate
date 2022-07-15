package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controllers.Validation;
import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    //Группа содержащая все фильмы
    private Map<Integer, Film> films = new HashMap<>();
    /**
     * Функция выполняющая валидацию
     */
    private Validation<Film, Film> validationFields = (someFilm) -> {
        if (someFilm.getName().isBlank()) {
            throw new ValidationException("Ошибка. Название не заполнено.");
        }
        if (someFilm.getDescription().length() > 200) {
            throw new ValidationException("Ошибка. Длинна описания больше 200 символов.");
        }
        if (someFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка. Дата релиза фильма не соответствует реальности.");
        }
        if (someFilm.getDuration() <= 0) {
            throw new ValidationException("Ошибка. Продолжительность фильма меньше или равна нулю.");
        }
        return someFilm;
    };

    /**
     * Счётчик содержит последний выданный идентификатор
     */
    private int idCount = 1;

    /**
     * Генератор идентификаторов
     * @return
     */
    private int idGenerator() {
        return idCount++;
    }

    /**
     * Создание и валидация экземпляров класса Film и добавление в группу
     * @param film
     * @return
     */
    @Override
    public Film addFilm(Film film) {
        //Присвоить фильму идентификатор
        int count = idCount;
        film = film.toBuilder().id(idGenerator()).build();

        //Добавление фильма в коллекцию если он прошёл валидацию иначе запись об ошибке в лог
        try {
            film.validate(validationFields);
            films.put(film.getId(), film);
            log.info("В коллекцию добавлен фильм {}", film);
            return film;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Обновление экземпляра класса Film
     * @param film
     * @return
     */
    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Фильм {} не будет обновлён. Такого фильма нет в коллекции.", film);
            throw new ValidationException("Ошибка обновления фильма. Такого фильма нет в коллекции.");
        }
        //Добавление фильма в коллекцию если он прошёл валидацию иначе запись об ошибке в лог
        try {
            //Проверка правильности заполнения полей объекта
            film.validate(validationFields);
            //Обновление объекта
            films.put(film.getId(), film);
            log.info("В коллекции обновлён фильм {}", film);
            return film;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Получение списка фильмов
     * @return
     */
    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
