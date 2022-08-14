package ru.yandex.practicum.filmorate.dao_impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    @Test
    void addFilm() {
        //Подготовка
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("GP-10");

        Genre genre = new Genre();
        genre.setId(1);
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Film 1 description")
                .releaseDate(LocalDate.EPOCH)
                .duration(120)
                .mpa(mpa1)
                .genres(List.of(genre))
                .build();

        Film expectedFilm = film1.toBuilder().id(1L).build();

        //Действия
        Optional<Film> actualFilm = filmDbStorage.addFilm(film1);
        //Проверка
        assertEquals(expectedFilm, actualFilm.get());


    }

    @Test
    void getFilms() {
        //Подготовка
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("PG-10");

        Genre genre = new Genre();
        genre.setId(1);
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Film 1 description")
                .releaseDate(LocalDate.EPOCH)
                .duration(120)
                .mpa(mpa1)
                .genres(List.of(genre))
                .build();

        Film expectedFilm = film1.toBuilder().id(1L).build();
        filmDbStorage.addFilm(film1);

        //Действия
        Film actualFilm = filmDbStorage.getFilms().get().get(0);

        //Проверка
        assertEquals(expectedFilm, actualFilm);
    }

    @Test
    void updateFilm() {
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("PG-10");

        Genre genre = new Genre();
        genre.setId(1);
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Film 1 description")
                .releaseDate(LocalDate.EPOCH)
                .duration(120)
                .mpa(mpa1)
                .genres(List.of(genre))
                .build();
        filmDbStorage.addFilm(film1);

        Genre genreNew = new Genre();
        genreNew.setId(2);
        Film newFilm1 = Film.builder()
                .id(1L)
                .name("Film 1 new")
                .description("Film 1 new description")
                .releaseDate(LocalDate.EPOCH)
                .duration(120)
                .mpa(mpa1)
                .genres(List.of(genreNew))
                .build();

        Film expectedFilm = newFilm1;

        //Действия
        Film actualFilm = filmDbStorage.updateFilm(newFilm1).get();

        //Проверка
        assertEquals(expectedFilm, actualFilm);
    }
}