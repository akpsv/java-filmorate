package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import ru.yandex.practicum.filmorate.model.User;

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

    public Optional<User> addUserToFriends(User user, long userId){
        Set<Long> newFriendsGroup = user.getUsers();
        if (newFriendsGroup.add(userId)) {
            user = user.toBuilder().users(newFriendsGroup).build();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public User removeUserFromFriends(User user, int userId){
        throw new NullPointerException("Method removeUserFromFriends not implemented");
    }

    public List<User> getFriends(User user){


        throw new NullPointerException("Method getFriends not implemented");
    }

}
