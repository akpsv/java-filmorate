package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
    }

    @Test
    void addUserToFriendsTest() {
        //Подготовка
        User user1 = User.builder()
                .id(1L)
                .name("User1")
                .login("User1Login")
                .birthday(LocalDate.of(2020, 03, 26))
                .email("user1@yandex.ru")
                .build();

        Set<Long> groupOfFriends1 = new HashSet<>();
//        groupOfFriends1.add(user1.getId());

        User user2 = User.builder()
                .id(2L)
                .name("User2")
                .login("User2Login")
                .birthday(LocalDate.of(2020, 04, 26))
                .email("user2@yandex.ru")
                .build();

        int expectedNumberFriends = 1;

        //Действия

        user2 = UserService.addUserToFriends(user2, user1.getId()).get();
        int actualNumberFriends = userService.getFriends(user2).size() ;

        //Проверка
        assertEquals(expectedNumberFriends, actualNumberFriends);
    }

    @Test
    void removeUserFromFriendsTest() {
        //Подготовка
        User user1 = User.builder()
                .id(1L)
                .name("User1")
                .login("User1Login")
                .birthday(LocalDate.of(2020, 03, 26))
                .email("user1@yandex.ru")
                .build();


        User user2 = User.builder()
                .id(2L)
                .name("User2")
                .login("User2Login")
                .birthday(LocalDate.of(2020, 04, 26))
                .email("user2@yandex.ru")
                .build();

        Set<Long> groupOfFriends3 = new HashSet<>();
        groupOfFriends3.add(user1.getId());
        groupOfFriends3.add(user2.getId());

        User user3 = User.builder()
                .id(3L)
                .name("User3")
                .login("User3Login")
                .birthday(LocalDate.of(2020, 05, 26))
                .email("user3@yandex.ru")
                .friends(groupOfFriends3)
                .build();

        int expectedNumberOfFriensd = 1;

        //Действия
        User userWithoutFriend = UserService.removeUserFromFriends(user3, user1.getId()).get();
        int actualNumberOfFriensd = userWithoutFriend.getFriends().size();

        //Проверка
        assertEquals(expectedNumberOfFriensd, actualNumberOfFriensd);
    }
}