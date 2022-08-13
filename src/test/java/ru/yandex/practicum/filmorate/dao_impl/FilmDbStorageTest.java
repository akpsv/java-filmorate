package ru.yandex.practicum.filmorate.dao_impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    @Test
    void addFilm() {
        //Подготовка
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Film 1 description")
                .releaseDate(LocalDate.EPOCH)
                .duration(120)
                .mpa(Map.of("id", 3))
                .genres(List.of(Map.of("id", 1)))
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
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Film 1 description")
                .releaseDate(LocalDate.EPOCH)
                .duration(120)
                .mpa(Map.of("id", 3))
                .genres(List.of(Map.of("id", 1)))
                .build();

        Film expectedFilm = film1.toBuilder().id(1L).build();
        filmDbStorage.addFilm(film1);

        //Действия
        Film actualFilm = filmDbStorage.getFilms().get().get(0);

        //Проверка
        assertEquals(expectedFilm, actualFilm);
    }
}