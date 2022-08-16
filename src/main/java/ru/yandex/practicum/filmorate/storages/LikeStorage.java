package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Optional;

public interface LikeStorage {
    Optional<List<Like>> getLikes();
    Optional<Like> addLike(long filmId, long userId);
    boolean deleteLike(long filmId, long userId);
}
