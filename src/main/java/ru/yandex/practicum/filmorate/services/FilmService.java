package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.Positive;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
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
     * Получить фильмы из группы
     *
     * @return
     */
    public Optional<List<Film>> getFilms() {
        return filmStorage.getFilms();
    }

    /**
     * Добавить фильм в группу
     *
     * @param film
     * @return
     */
    public Optional<Film> addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    /**
     * Обновить существующий фильм в группе
     *
     * @param film
     * @return
     */
    public Optional<Film> updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    /**
     * Добавить лайк в группу.
     * Лайк добавляется косвенно, через добавление идентификатора пользователя его поставившего
     */
    public boolean addLike(long id, long userId) {
        //Если фильма нет то возращается NoSuchElementException
        Film film = getFilmById(id).get();
        Set<Long> likes = film.getLikes();
        //Если в списке нет ни одного лайка и соответственно вместо списка null, то присваивается список
        if (likes == null) {
            likes = new HashSet<>();
        }
        //TODO: Добавить проверку пользователя на существование
        if (likes.add(userId)) {
            film = film.toBuilder().likes(likes).build();
            filmStorage.updateFilm(film);
            return true;
        }
        return false;
    }

    /**
     * Получить count фильмов в порядке уменьшения количества лайков
     *
     * @return
     */
    public Optional<List<Film>> getBestFilms(int count) {
        //Может выдавать NoSuchElementException, ValidationException
        if (count <= 0) {
            //Стараться получить ноль или меньше фильмов не имеет смысла
            //В целях единообразия выбрасывается исключение
            throw new ValidationException("Значение count не может быть меньше 1.");
        }
        //Сравнение фильмов по количеству лайков в возрастающем порядке
        Comparator<Film> compareLikes = Comparator.comparing(film -> film.getLikes().size());
        //Возврат фильмов в убывающем по количеству лайков порядке
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
        //Может выдавать NoSuchElementException
        return filmStorage.getFilms().get().stream()
                .filter((film) -> film.getId() == id)
                .findAny();
    }

    /**
     * Удалить лайк
     *
     * @param id
     * @param userId
     * @return
     */
    public boolean deleteLike(long id, long userId) {
        //Может выдавать NoSuchElementException
        Film film = getFilmById(id).get();

        Set<Long> likes = film.getLikes();
        if (likes.remove(userId)) {
            film = film.toBuilder().likes(likes).build();
            return true;
        }
        throw new NoSuchElementException("Лайк для удаления не найден. Такой пользователь не ставил лайк");
    }
}
