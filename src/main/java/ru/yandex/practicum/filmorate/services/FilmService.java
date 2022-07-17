package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
//    будет отвечать за операции с фильмами,
//    — добавление лайк,
//    - удаление лайка,
//    - вывод 10 наиболее популярных фильмов по количеству лайков.

//    Пусть пока каждый пользователь может поставить лайк фильму только один раз.

    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    /**
     * Вернуть используемый экземпляр хранилища фильмов
     *
     * @return
     */
    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    /**
     * Добавить лайк в группу.
     * Лайк добавляется косвенно, через добавление идентификатора пользователя его поставившего
     */
    public static Optional<Film> addLike(Film film, long userId) {
        Set<Long> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
            likes.add(userId);
            film = film.toBuilder().likes(likes).build();
            return Optional.of(film);
        }
        if (likes.add(userId)) {
            film = film.toBuilder().likes(likes).build();
            return Optional.of(film);
        }
        //Если лайк не добавился, то возвращается пустота
        return Optional.empty();
    }

    /**
     * Удалить лайк из группы
     *
     * @param film
     * @param userId
     * @return
     */
    public static Optional<Film> removeLike(Film film, long userId) {
        Set<Long> likes = film.getLikes();
        if (likes == null) {
            return Optional.empty();
        }
        if (likes.remove(userId)) {
            film = film.toBuilder().likes(likes).build();
            return Optional.of(film);
        }
        return Optional.empty();
    }

    /**
     * Получить 10 фильмов в порядке уменьшения количества лайков
     *
     * @return
     */
    public Optional<List<Film>> getBestFilms(int count) {
        if (count <= 0) {
            count = 10;
        }
        //Сравнение фильмов по количеству лайков в возрастающем порядке
        Comparator<Film> compareLikes = Comparator.comparing(film -> film.getLikes().size());
        //Возврат 10 фильмов в убывающем по количеству лайков порядке
        List<Film> films = filmStorage.getFilms().stream()
                .sorted(compareLikes.reversed())
                .limit(count)
                .collect(Collectors.toList());
        return Optional.of(films);
    }

    /**
     * Получить фильма по идентификатору
     *
     * @param id - идентификатор искомого фильма
     * @return - фильм или ничего
     */
    public Optional<Film> getFilmById(long id) {
        return filmStorage.getFilms().stream()
                .filter((film) -> film.getId() == id)
                .findAny();
    }
}
