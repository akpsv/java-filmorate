package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Добавить пользователя в группу
     * @param user
     * @return
     */
    @PostMapping()
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * Обновить пользователя в группе
     * @param user
     * @return
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Получить группу пользователей
     * @return
     */
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * Получить пользователя по идентификатору
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id).get();
    }

    /**
     * Добавить пользователя в друзья
     *
     * @param id       - идентификатор пользователя которому добавляется друг
     * @param friendId - идентификатор добавляемого друга
     * @return - true если друг добавлен, false если друг уже был в списке
     * ислкючение NoSuchElementException если пользователь не найден
     */
    @PutMapping("/{id}/friends/{friendId}")
    public boolean addUserToFriends(@PathVariable long id, @PathVariable long friendId) {
        return userService.addUserToFriends(id, friendId);
    }

    /**
     * Удалить пользователя из друзей
     *
     * @param id       - идентификатор пользователя чьего друга надо удалить
     * @param friendId - идентификатор удаляемого друга
     * @return - true, если друг удалён. false, если друга не было в списке.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteUserFromFriends(@PathVariable long id, @PathVariable long friendId) {
        return userService.deleteUserFromFriends(id, friendId);
    }

    /**
     * Вернуть список друзей пользователя
     *
     * @param id - идентификатор пользователя чьих друзей надо вернуть
     * @return - список друзей
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriendsForUser(@PathVariable long id) {
        return userService.getFriendsForUser(id);
    }

    /**
     * Получить список друзей, общих с другим пользователем
     *
     * @param id
     * @param otherId
     * @return
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendsForUser(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriendsForUser(id, otherId);
    }
}
