package ru.yandex.practicum.filmorate.dao_impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    @Test
    public void addUser() {
        //Подготовка
        User user1 = User.builder()
                .name("User 1")
                .login("UserLogin1")
                .email("user1@mail.ru")
                .birthday(LocalDate.EPOCH)
                .friends(new HashSet<>())
                .build();

        User expectedUser = user1.toBuilder().id(1L).build() ;
        //Действия
        User actualUser = userDbStorage.addUser(user1).get();

        //Проверка
        assertEquals(expectedUser, actualUser);

    }

    @Test
    void updateUser() {
        //Подготовка
        User user1 = User.builder()
                .name("User 1")
                .login("UserLogin1")
                .email("user1@mail.ru")
                .birthday(LocalDate.EPOCH)
                .friends(new HashSet<>())
                .build();
        User addedUser = userDbStorage.addUser(user1).get();

        User updatedUser1 = User.builder()
                .id(addedUser.getId())
                .name("User 1 updated")
                .login("UserLogin1updated")
                .email("user1updated@mail.ru")
                .birthday(LocalDate.EPOCH)
                .friends(new HashSet<>())
                .build();

        User expectedUser = updatedUser1;

        //Действия
        User actualUser = userDbStorage.updateUser(updatedUser1).get();

        //Проверка
        assertEquals(expectedUser, actualUser);

    }

    @Test
    void getUsers() {
        //Подготовка
        User user1 = User.builder()
                .name("User 1")
                .login("UserLogin1")
                .email("user1@mail.ru")
                .birthday(LocalDate.EPOCH)
                .friends(new HashSet<>())
                .build();

        userDbStorage.addUser(user1).get();

        //Действия
        int actualSizeOfUserGroup = userDbStorage.getUsers().get().size();

        //Проверка
        assertEquals(3, actualSizeOfUserGroup);
    }
}