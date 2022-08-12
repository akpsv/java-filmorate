package ru.yandex.practicum.filmorate.dao_impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("dbStorage")
public class UserDbStorage implements UserStorage {
    private JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Добавить пользователя в БД
     * @param user - объект пользователя
     * @return
     */
    @Override
    public Optional<User> addUser(User user){
        SimpleJdbcInsert insertUserData = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long userId = insertUserData.executeAndReturnKey(user.toMap()).longValue();
        user = user.toBuilder().id(userId).build();
        return Optional.of(user);
    }

    /**
     * Обновить пользователя
     * @param user
     * @return
     */
    @Override
    public Optional<User> updateUser(User user) {
        //Обновить друзей
        updateFriends(user);

        String sqlUpdateUser = "UPDATE users SET name=?, login=?, email=?, birthday=?  WHERE user_id = ?";
        int update = jdbcTemplate.update(sqlUpdateUser,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());

        if (update==1){
            return Optional.of(user);
        }else {
            return Optional.empty();
        }
    }

    /**
     * Обновить список идентификаторов друзей идентификатором нового друга
     * @param user - пользователь к которому добавляем друга
     * @return true - если друг добалвен, иначе - false
     */
    private boolean updateFriends(User user){
        if (user.getFriends()==null || user.getFriends().isEmpty()){
            return false;
        }
        //Получить идентификаторы друзей пользователя находящиеся в БД
        String sqlSelectFriends = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Long> friendsIdFromDB = jdbcTemplate.query(sqlSelectFriends,
                (rs, rowNum) -> rs.getLong("friend_id"),
                user.getId());
        //Если друзеё в объекте Пользователь больше чем в БД , то добавить друга иначе удалить
        if (user.getFriends().size()> friendsIdFromDB.size()) {
            return addFriendToUser(user, friendsIdFromDB);
        } else if (user.getFriends().size()< friendsIdFromDB.size()) {
           return deleteFriendFromUser(user, friendsIdFromDB);
        } else {
            return false;
        }
    }

    /**
     * Добавить друга пользователю
     * @param user
     * @param friendsIdFromDB
     * @return
     */
    private boolean addFriendToUser(User user, List<Long> friendsIdFromDB){
        //Получить множество идентификаторов пользователей с добавленным другом
        //TODO: что-то сделать с возможным null
        Set<Long> setWithNewFriend = user.getFriends();
        //Получить идентификатор добавленного друга
        setWithNewFriend.removeAll(friendsIdFromDB);
        if (setWithNewFriend.isEmpty()) {
            return false;
        }
        long newFriendId = setWithNewFriend.stream().findFirst().get();
        //Вставить идентификатор нового друга
        String sqlInsertFriend = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlInsertFriend, user.getId(), newFriendId);
        return true;
    }

    /**
     * Удалить друга у пользователя
     * @param user
     * @param friendsIdFromDB
     * @return
     */
    private boolean deleteFriendFromUser(User user, List<Long> friendsIdFromDB){
        //Получить множество идентификаторов пользователей с добавленным другом
        //TODO: что-то сделать с возможным null
        Set<Long> setWithoutFriend = user.getFriends();
        //Получить идентификатор добавленного друга
        friendsIdFromDB.removeAll(setWithoutFriend);
        long deletingFriendId = friendsIdFromDB.get(0);

        //Вставить идентификатор нового друга
        String sqlDeleteFriend = "DELETE FROM friends WHERE user_id = ? AND friend_id= ? ";
        jdbcTemplate.update(sqlDeleteFriend, user.getId(), deletingFriendId);
        return true;
    }

    /**
     * Получить из БД и преобразовать в объекты всех пользователей
     * @return
     */
    @Override
    public Optional<List<User>> getUsers() {
        //Получить всех пользователей
        String sqlSelectAllUsers = "SELECT * FROM users";
        List<User> userList = jdbcTemplate.query(sqlSelectAllUsers, this::mapRowToUser);

        return Optional.of(userList);
    }





    /**
     * Создать объект пользователя из строки БД
     * @param resultSet
     * @param rowNum
     * @return
     * @throws SQLException
     */
    private User mapRowToUser(ResultSet resultSet, int rowNum)throws SQLException{
        //Получить друзей пользователя
        Set<Long> friends_id = getFriendsForUser(resultSet.getLong("user_id"));
        //Сформировать объект пользователя
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(friends_id)
                .build();
    }

    /**
     * Получить друзей пользователя
     * @param id
     * @return
     */
    private Set<Long> getFriendsForUser(Long id) {
        String sqlSelectFriends = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Long> friends_id = jdbcTemplate.query(sqlSelectFriends, (rs, rowNum) -> rs.getLong("friend_id"), id);
        return friends_id.stream().collect(Collectors.toSet());
    }
}
