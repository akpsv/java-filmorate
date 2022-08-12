package ru.yandex.practicum.filmorate.dao_impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> addFilm(Film film) {
        SimpleJdbcInsert insertUserData = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long filmId = insertUserData.executeAndReturnKey(film.toMap()).longValue();
        film = film.toBuilder().id(filmId).build();
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Film>> getFilms() {
        //Получить всех пользователей
        String sqlSelectAllUsers = "SELECT * FROM films";
        List<User> userList = jdbcTemplate.query(sqlSelectAllUsers, this::mapRowToUser);

        return Optional.of(userList);
    }

    /**
     * Создать объект пользователя из строки БД
     * @param resultSet
     * @param rowNum
     * @return
     * @throws SQLException
     */
    private Film mapRowToUser(ResultSet resultSet, int rowNum)throws SQLException{
        //Получить друзей пользователя
        Set<Long> idOfLikes = getLikesForFilm(resultSet.getLong("user_id"));
        getRatingForFilm();
        getGenreForFilm();

        //Сформировать объект пользователя
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(friends_id)
                .build();
    }
}
