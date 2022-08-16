package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.List;

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
     *
     * @param user
     * @return
     */
    @PostMapping()
    public User addUser(@RequestBody User user) {
        return userService.addUser(user).get();
    }

    /**
     * Обновить пользователя в группе
     *
     * @param user
     * @return
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Получить группу пользователей
     *
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
     * Вернуть список друзей пользователя
     *
     * @param id - идентификатор пользователя чьих друзей надо вернуть
     * @return - список друзей
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
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
