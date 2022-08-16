package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.Validation;
import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private UserStorage userStorage;
    /**
     * Функция производящая валидацию полей объекта
     */
    private Validation<User, User> validationFields = (someUser) -> {
        if (someUser.getEmail().isBlank() || !someUser.getEmail().contains("@")) {
            throw new ValidationException("Ошибка. Поле электронной почты пусто или не содержит знак @.");
        }
        if (someUser.getLogin().isBlank() || someUser.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка. Поле логин пусто или содержит пробелы.");
        }
        if (someUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка. Дата дня рождения не может быть в будущем.");
        }
        return someUser;
    };
    /**
     * Функция проверки поля имени и если оно пусто то установки в него значения из поля логин
     */
    private Validation<User, User> changeNameIfBlank = (someUser) -> {
        if (someUser.getName().isBlank()) {
            return someUser.toBuilder().name(someUser.getLogin()).build();
        }
        return someUser;
    };

    @Autowired
    public UserService(@Qualifier("dbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Добавить пользователя в друзья
     *
     * @param user
     * @param userId
     * @return
     */
    public static Optional<User> addUserToFriends(User user, long userId) {
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

    /////////////////////////////////////////////////////////////

    /**
     * Удалить пользователя из списка друзей
     *
     * @param user
     * @param userId
     * @return
     */
    public static Optional<User> removeUserFromFriends(User user, long userId) {
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
     *
     * @param user
     * @return
     */
    public static List<Long> getFriends(User user) {
        return new ArrayList<>(user.getFriends());
    }

    /**
     * Добавить пользователя в грппу
     *
     * @param user
     * @return
     */
    public Optional<User> addUser(User user) {
        //Произвести валидацию пользователя и добавить его в группу,
        // если валидация не пройдена залогировать и выбросить исключение
        try {
            //Проверить поля на соответствие требованиям
            user.validate(validationFields);
            //Проверить поле имени. Если оно не заполнено, то установить значение из поля логин
            user = user.validate(changeNameIfBlank);
            //Присвоить пользователю идентификатор
            Optional<User> addedUser = userStorage.addUser(user);
            log.info("В группу пользователей добавлен пользователь: {}", user.getLogin());
            return addedUser;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Обновить существующего пользователя в группе
     *
     * @param user
     * @return
     */
    public User updateUser(User user) {
        return userStorage.updateUser(user).get();
    }

    /**
     * Получить пользователей из группы
     *
     * @return
     */
    public List<User> getUsers() {
        return userStorage.getUsers().get();
    }

    /**
     * Получить пользователя по идентификатору
     *
     * @param id - идентификатор искомого пользователя
     * @return - возвращается пользователь или ничего
     */
    public Optional<User> getUserById(long id) {
        return userStorage.getUsers().get().stream()
                .filter(user -> user.getId() == id)
                .findAny();
    }

    public boolean addUserToFriends(long id, long friendId) {
        //Если пользователь не найден вернётся исключение NoSuchElementException
        User mainUser = getUserById(id).get();
        //Получить группу друзей основного пользователя
        Set<Long> friendsOfMainUser = mainUser.getFriends();
        if (friendsOfMainUser == null) {
            friendsOfMainUser = new HashSet<>();
        }
        //Если такого друга не существует вернётся исключение NoSuchElementException
        User friendUser = getUserById(friendId).get();

        if (friendsOfMainUser.add(friendId)) {
            mainUser = mainUser.toBuilder().friends(friendsOfMainUser).build();
            userStorage.updateUser(mainUser);
            return true;
        }
        return false;
    }

    public List<User> getFriendsForUser(long id) {
        //Может выдавать NoSuchElementException
        User user = getUserById(id).get();
        return user.getFriends().stream()
                .map(this::getUserById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsForUser(long id, long otherId) {
        //Может выдавать NoSuchElementException
        User user = getUserById(id).get();
        if (user.getFriends() == null) {
            user = user.toBuilder().friends(new HashSet<>()).build();
        }
        Set<Long> friendsBoth = new HashSet<>();
        friendsBoth.addAll(user.getFriends());
        friendsBoth.retainAll(getUserById(otherId).get().getFriends());
        return friendsBoth.stream()
                .map(this::getUserById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public boolean deleteUserFromFriends(long id, long friendId) {
        //Если нет пользователя у которого из друзей удаляется друг, то выбрасывается исключение NoSuchElementException
        User user = getUserById(id).get();
        Set<Long> friends = user.getFriends();
        //Если друг удалился, то вернуть true, если его не было в списке, то вернуть false
        if (friends.remove(friendId)) {
            user = user.toBuilder().friends(friends).build();
            //Если друг удалён то истина, иначе ложь
            if (userStorage.updateUser(user).isPresent()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
