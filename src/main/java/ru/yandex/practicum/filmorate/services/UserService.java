package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.*;

@Service
public class UserService {
//    будет отвечать за такие операции с пользователями, как
//
//    добавление в друзья,
//    удаление из друзей,
//    вывод списка общих друзей.
//
//    Пока пользователям не надо
//    одобрять заявки в друзья — добавляем сразу. То есть если Лена стала другом Саши,
//    то это значит, что Саша теперь друг Лены.

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user){
        return userStorage.addUser(user).get();
    }

    public User updateUser(User user){
        return userStorage.updateUser(user).get();
    }

    public List<User> getUsers(){
        return userStorage.getUsers().get();
    }

    /**
     * Добавить пользователя в друзья
     * @param user
     * @param userId
     * @return
     */
    public static Optional<User> addUserToFriends(User user, long userId){


        Set<Long> newFriendsGroup = user.getFriends();
        //Если в поле нет списка друзей, а соответственно нет и друзей, надо добавить идентификатор друга
        if (newFriendsGroup == null) {
            newFriendsGroup = new HashSet<>();
            newFriendsGroup.add(userId);
            user = user.toBuilder().friends(newFriendsGroup).build();
            return Optional.of(user);
        }
        if (newFriendsGroup.add(userId)) {
            user = user.toBuilder().friends(newFriendsGroup).build();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Удалить пользователя из списка друзей
     * @param user
     * @param userId
     * @return
     */
    public static Optional<User> removeUserFromFriends(User user, long userId){
        Set<Long> friendsOfUser = user.getFriends();
        if (friendsOfUser == null) {
            return Optional.empty();
        }
        if (friendsOfUser.remove(userId)) {
            user = user.toBuilder().friends(friendsOfUser).build();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Получить всех друзей
     * @param user
     * @return
     */
    public static List<Long> getFriends(User user){
        return new ArrayList<>(user.getFriends());
    }

    /**
     * Получить пользователя по идентификатору
     * @param id - идентификатор искомого пользователя
     * @return - возвращается пользователь или ничего
     */
    public Optional<User> getUserById(long id){
        return userStorage.getUsers().get().stream()
                .filter(user -> user.getId() == id)
                .findAny();
    }

}
