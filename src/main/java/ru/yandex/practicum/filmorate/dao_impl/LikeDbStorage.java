package ru.yandex.practicum.filmorate.dao_impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storages.LikeStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получить все лайки ко всем фильмам
     * @return
     */
    @Override
    public Optional<List<Like>> getLikes() {
        String sqlSelectLikes = "SELECT * FROM likes";
        List<Like> likes = jdbcTemplate.query(sqlSelectLikes,
                (rs, rowNum) -> new Like(rs.getInt("film_id"), rs.getInt("user_id")));
        return Optional.of(likes);
    }

    /**
     * Добавить лайк фильму
     * @param filmId - ид фильма
     * @param userId - ид пользователя
     * @return
     */
    @Override
    public Optional<Like> addLike(long filmId, long userId) {

        String sqlInsertLike = "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";
        int isUpdated = jdbcTemplate.update(sqlInsertLike, filmId, userId);
        if (isUpdated==1){
            return Optional.of(new Like(filmId, userId));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Удалить лайк фильму
     * @param filmId - ид фильма
     * @param userId - ид пользователя
     * @return
     */
    @Override
    public boolean deleteLike(long filmId, long userId) {
        String sqlDeleteLike = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        int isDeleted = jdbcTemplate.update(sqlDeleteLike, filmId, userId);
        if (isDeleted==1){
            return true;
        } else {
            return false;
        }
    }
}
