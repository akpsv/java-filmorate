package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.services.FriendService;

@RestController
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * Добавить пользователя в друзья
     *
     * @param id       - идентификатор пользователя которому добавляется друг
     * @param friendId - идентификатор добавляемого друга
     * @return - true если друг добавлен, false если друг уже был в списке
     * ислкючение NoSuchElementException если пользователь не найден
     */
    @PutMapping("/users/{id}/friends/{friendId}")
    public Friend addFriend(@PathVariable long id, @PathVariable long friendId) {
        return friendService.addFriend(id, friendId).get();
    }

    /**
     * Удалить пользователя из друзей
     *
     * @param id       - идентификатор пользователя чьего друга надо удалить
     * @param friendId - идентификатор удаляемого друга
     * @return - true, если друг удалён. false, если друга не было в списке.
     */
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        return friendService.deleteFriend(id, friendId);
    }

}
