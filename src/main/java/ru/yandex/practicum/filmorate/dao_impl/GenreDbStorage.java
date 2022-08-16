package ru.yandex.practicum.filmorate.dao_impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storages.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<List<Genre>> getGenres() {
        String sqlSelectGenres = "SELECT * FROM genres";
        List<Genre> genres = jdbcTemplate.query(sqlSelectGenres, this::mapRowToGenre);
        return Optional.of(genres);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        String sqlSelectGenreById = "SELECT * FROM genres WHERE genre_id = ?";
        Genre genre = jdbcTemplate.queryForObject(sqlSelectGenreById, this::mapRowToGenre, id);
        return Optional.of(genre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        //Получить идентификатор фильма
        int genreId = resultSet.getInt("genre_id");
        String genre_name = resultSet.getString("genre_name");

        //Сформировать объект пользователя
        return new Genre(genreId, genre_name);
    }
}
