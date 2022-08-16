package ru.yandex.practicum.filmorate.dao_impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storages.FriendStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<List<Friend>> getFriends(long userId) {
        String sqlSelectFriends = "SELECT * FROM friends WHERE user_id = ?";
        List<Friend> friends = jdbcTemplate.query(sqlSelectFriends,
                (rs, rowNum) -> new Friend(rs.getLong("user_id"), rs.getLong("friend_id")),
                userId);
        return Optional.of(friends);
    }

    @Override
    public Optional<Friend> addFriend(long userId, long friendId) {
        String sqlInsertFriend = "INSERT INTO friends(user_id, friend_id) VALUES(?, ?)";
        int isInserted = jdbcTemplate.update(sqlInsertFriend, userId, friendId);
        if (isInserted == 1) {
            return Optional.of(new Friend(userId, friendId));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        String sqlDeleteFriend = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int isDeleted = jdbcTemplate.update(sqlDeleteFriend, userId, friendId);
        if (isDeleted == 1) {
            return true;
        } else {
            return false;
        }
    }
}
