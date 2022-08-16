package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;
import java.util.Optional;

public interface FriendStorage {
    Optional<List<Friend>> getFriends(long userId);

    Optional<Friend> addFriend(long userId, long friendId);

    boolean deleteFriend(long userId, long friendId);
}
