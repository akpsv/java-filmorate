package ru.yandex.practicum.filmorate.dao_impl;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import java.util.List;
import java.util.Optional;

public class FilmDbStorage implements FilmStorage {
    @Override
    public Optional<Film> addFilm(Film film) {
        return Optional.empty();
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Film>> getFilms() {
        return Optional.empty();
    }
}
