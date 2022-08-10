package ru.yandex.practicum.filmorate.dao_impl;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.List;
import java.util.Optional;

public class UserDbStorage implements UserStorage {
    @Override
    public Optional<User> addUser(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> updateUser(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<List<User>> getUsers() {
        return Optional.empty();
    }
}
