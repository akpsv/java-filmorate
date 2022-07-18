package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    @Test
    void addLikeTest() {
        //Подготовка
        //Проверка добавления к пустому списку
        Set<Long> likes = new HashSet<>();
        likes.add(1L);

        Film film1 = Film.builder()
                .id(1L)
                .name("Some name")
                .description("Some description")
                .duration(60)
                .releaseDate(LocalDate.EPOCH)
                .build();
        int expectedNumberOfLikes = 1;

        //Действия
//        film1 = FilmService.addLike(film1.getId(), 1L).get();
//        int actualNumberOfLikes = film1.getLikes().size();

        //Проверка
//        assertEquals(expectedNumberOfLikes, actualNumberOfLikes);

        //Проверить добалвние к непустому списку
        Film film2 = Film.builder()
                .id(2L)
                .name("Some name")
                .description("Some description")
                .duration(60)
                .releaseDate(LocalDate.EPOCH)
                .likes(likes)
                .build();
        int expectedNumberOfLikes2 = 2;

        //TODO: переделать тест
        //Действия
//        film2 = FilmService.addLike(film2, 2L).get();
//        int actualNumberOfLikes2 = film2.getLikes().size();

        //Проверка
//        assertEquals(expectedNumberOfLikes2, actualNumberOfLikes2);

    }

    @Test
    void removeLikeTest() {
        //Подготовка
        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        likes.add(2L);

        Film film2 = Film.builder()
                .id(2L)
                .name("Some name")
                .description("Some description")
                .duration(60)
                .releaseDate(LocalDate.EPOCH)
                .likes(likes)
                .build();

        int expectedNumberLikes =1;

        //Действия
        film2 = FilmService.removeLike(film2, 1).get();
        int actualNumberLikes = film2.getLikes().size();

        //Проверка
        assertEquals(expectedNumberLikes, actualNumberLikes);
    }

    @Test
    void getTenBestFilms() {
        //Подготовка
        //Подготовка
        Set<Long> likesFilm1 = new HashSet<>();
        likesFilm1.add(1L);
        likesFilm1.add(2L);

        Film film1 = Film.builder()
                .id(1L)
                .name("Some name")
                .description("Some description")
                .duration(60)
                .releaseDate(LocalDate.EPOCH)
                .likes(likesFilm1)
                .build();

        Set<Long> likesFilm2 = new HashSet<>();
        likesFilm2.add(1L);
        likesFilm2.add(2L);
        likesFilm2.add(3L);
        likesFilm2.add(4L);

        Film film2 = Film.builder()
                .id(2L)
                .name("Some name")
                .description("Some description")
                .duration(60)
                .releaseDate(LocalDate.EPOCH)
                .likes(likesFilm2)
                .build();

        FilmService filmService = new FilmService(new InMemoryFilmStorage());
        filmService.getFilmStorage().addFilm(film1);
        filmService.getFilmStorage().addFilm(film2);

        List<Film> film21 = List.of(film2, film1);
        List<Film> film12 = List.of(film1, film2);

        //Действия
        //Проверка порядка до выбора 10 лучших по количесву лайков фильмов
        assertArrayEquals(film12.toArray(), filmService.getFilmStorage().getFilms().get().toArray());

        //Выбор 10 лучших по количесву лайков фильмов
        List<Film> tenBestFilms = filmService.getBestFilms(10).get();

        //Проверка
        assertArrayEquals(film21.toArray(), tenBestFilms.toArray());
    }
}