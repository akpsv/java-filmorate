package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> addUser(User user);

    Optional<User> updateUser(User user);

    Optional<List<User>> getUsers();

    boolean deleteUserById(int Id);
}
