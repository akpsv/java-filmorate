package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FriendService;

import java.util.List;

@RestController
public class FriendController {
    private final FriendService friendService;


    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * Вернуть список друзей пользователя
     *
     * @param id - идентификатор пользователя чьих друзей надо вернуть
     * @return - список друзей
     */
    @GetMapping("/users/{id}/friends")
    public List<Friend> getFriends(@PathVariable long id) {
        return friendService.getFriends(id).get();
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
