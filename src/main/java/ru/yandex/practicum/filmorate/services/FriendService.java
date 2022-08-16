package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storages.FriendStorage;

import java.util.Optional;

@Service
public class FriendService {
    private final FriendStorage friendStorage;

    public FriendService(FriendStorage friendStorage) {
        this.friendStorage = friendStorage;
    }

    public Optional<Friend> addFriend(long userIid, long friendId) {
        if (friendId < 1) {
            return Optional.empty();
        } else {
            return friendStorage.addFriend(userIid, friendId);
        }
    }

    public boolean deleteFriend(long userId, long friendId) {
        return friendStorage.deleteFriend(userId, friendId);
    }
}
