package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.services.LikeService;
import ru.yandex.practicum.filmorate.storages.LikeStorage;

@RestController
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /**
     * Добавить лайк фильму от пользователя
     *
     * @param id - идентфикатор фильма
     * @param userId - идентификатор пользователя поставившего лайк
     * @return Like - объект лайк
     */
    @PutMapping("/films/{id}/like/{userId}")
    public Like addLike(@PathVariable long id, @PathVariable long userId) {
        return likeService.addLike(id, userId).get();
    }

    /**
     * Удалить лайк фульму от пользователя
     *
     * @param id - ид фильма
     * @param userId - ид пользователя
     * @return boolean - true если получилось удалить, false если не получилось удалить
     */
    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable long id, @PathVariable long userId) {
        return likeService.deleteLike(id, userId);
    }

}
