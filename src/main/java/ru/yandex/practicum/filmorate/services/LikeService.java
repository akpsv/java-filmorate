package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storages.LikeStorage;

import java.util.Optional;

@Service
public class LikeService {
    private final LikeStorage likeStorage;

    public LikeService(LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    public Optional<Like> addLike(long id, long userId) {
        return likeStorage.addLike(id, userId);
    }

    public boolean deleteLike(long filmId, long userId) {
        return likeStorage.deleteLike(filmId, userId);
    }

}
