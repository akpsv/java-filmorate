package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.Validation;
import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    //Группа содержащая все фильмы
    private Map<Long, Film> films = new HashMap<>();
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
     *
     * @return
     */
    private long idGenerator() {
        return idCount++;
    }

    /**
     * Создание и валидация экземпляров класса Film и добавление в группу
     *
     * @param film
     * @return
     */
    @Override
    public Optional<Film> addFilm(Film film) {
        //Добавление фильма в коллекцию если он прошёл валидацию иначе запись об ошибке в лог
        try {
            film.validate(validationFields);
            //Присвоить фильму идентификатор
            film = film.toBuilder().id(idGenerator()).likes(new HashSet<>()).build();
            //Добавить фмльм в общую группу
            films.put(film.getId(), film);
            log.info("В коллекцию добавлен фильм {}", film);
            return Optional.of(film);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Обновление экземпляра класса Film
     *
     * @param film
     * @return
     */
    @Override
    public Optional<Film> updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Фильм {} не будет обновлён. Такого фильма нет в коллекции.", film);
            throw new NoSuchElementException("Ошибка обновления фильма. Такого фильма нет в коллекции.");
        }
        //Добавление фильма в коллекцию если он прошёл валидацию иначе запись об ошибке в лог
        try {
            //Проверка правильности заполнения полей объекта
            film.validate(validationFields);
            //Создание хэш-множества если поле лайков содержит null
            if (film.getLikes() == null) {
                film = film.toBuilder().likes(new HashSet<>()).build();
            }
            //Обновление объекта
            films.put(film.getId(), film);
            log.info("В коллекции обновлён фильм {}", film);
            return Optional.of(film);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Получение списка фильмов
     *
     * @return
     */
    @Override
    public Optional<List<Film>> getFilms() {
        return Optional.of(new ArrayList<>(films.values()));
    }
}
