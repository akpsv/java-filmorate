package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.InMemoryUserStorage;

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

    public Optional<List<Film>> getFilms(){
        return filmStorage.getFilms();
    }

    public Optional<Film> addFilm(Film film){
        return filmStorage.addFilm(film);
    }

    public Optional<Film> updateFilm(Film film){
        return filmStorage.updateFilm(film);
    }

    /**
     * Добавить лайк в группу.
     * Лайк добавляется косвенно, через добавление идентификатора пользователя его поставившего
     */

    public boolean addLike(long id, long userId){
        //Если фильма нет то возращается NoSuchElementException
        Film film = getFilmById(id).get();
        Set<Long> likes = film.getLikes();
        //Если в списке нет ни одного лайка и соответственно вместо списка null, то присваивается список
        if (likes == null){
            likes = new HashSet<>();
        }
        if (likes.add(userId)) {
            film = film.toBuilder().likes(likes).build();
            filmStorage.updateFilm(film);
            return true;
        }
        return false;
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
        List<Film> films = filmStorage.getFilms().get().stream()
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
        return filmStorage.getFilms().get().stream()
                .filter((film) -> film.getId() == id)
                .findAny();
    }

    public boolean deleteLike(long id, long userId) {
        //TODO: handle NoSuchElementException
        Film film = getFilmById(id).get();

        Set<Long> likes = film.getLikes();
        if (likes.remove(userId)) {
            film = film.toBuilder().likes(likes).build();
            return true;
        }
        throw new NoSuchElementException("Лайк для удаления не найден. Такой пользователь не ставил лайк");
    }
}
