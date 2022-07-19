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
}