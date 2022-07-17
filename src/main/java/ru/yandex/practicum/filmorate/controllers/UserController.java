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

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public User addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers(){
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
     *          ислкючение NoSuchElementException если пользователь не найден
     */
    @PutMapping("/{id}/friends/{friendId}")
    public boolean addUserToFriends(@PathVariable long id, @PathVariable long friendId) {
        //TODO: process exception
        //Если пользователь не найден вернётся исключение NoSuchElementException
        User userById = userService.getUserById(id).get();
        //Получить группу друзей
        Set<Long> friends = userById.getFriends();
        //Добавить идентификатор нового друга в группу, и вернуть true если его идентификатора ещё нет в группе, иначе вернуть false
        if (friends.add(friendId)) {
            userById = userById.toBuilder().friends(friends).build();
            return true;
        }
        return false;
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
        //TODO: handle exception
        //Если нет пользователя у которого из друзей удаляется друг, то выбрасывается исключение NoSuchElementException
        User user = userService.getUserById(id).get();
        Set<Long> friends = user.getFriends();
        //Если друг удалился, то вернуть true, если его не было в списке, то вернуть false
        if (friends.remove(friendId)) {
            user = user.toBuilder().friends(friends).build();
            return true;
        }
        return false;
    }

    /**
     * Вернуть список друзей пользователя
     *
     * @param id - идентификатор пользователя чьих друзей надо вернуть
     * @return - список друзей
     */
    @GetMapping("/{id}/friends")
    public Set<Long> getFriendsForUser(@PathVariable long id) {
        //TODO: handle NoSuchElementException
        User user = userService.getUserById(id).get();
        return user.getFriends();
    }

    /**
     * Получить список друзей, общих с другим пользователем
     *
     * @param id
     * @param otherId
     * @return
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<Long> getCommonFriendsForUser(@PathVariable long id, @PathVariable long otherId) {
        //TODO: handle NoSuchElementException
        User user = userService.getUserById(id).get();
        Set<Long> friendsBoth = new HashSet<>(user.getFriends());
        friendsBoth.retainAll(userService.getUserById(otherId).get().getFriends());
        return friendsBoth;
    }
}
