package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    //Переменная содержит экземпляр проверяемого класса
    UserController userController;

    /**
     * Создаётся и присваивается экземпляр класса
     */
    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    /**
     * Проверяется создание и добавление пользователя в группу
     */
    @Test
    void addUserTest() {
        //Подготовка
        User user1 = User.builder()
                .id(1)
                .name("User")
                .login("UserLogin")
                .birthday(LocalDate.of(2020, 03, 26))
                .email("user@yandex.ru")
                .build();
        int expectedSizeOfGroup = 1;

        //Действия
        userController.addUser(user1);
        int actualSizeOfGroup = userController.getUsers().size();

        //Проверка
        assertEquals(expectedSizeOfGroup, actualSizeOfGroup);
    }

    /**
     * Проверка работы ограничений на содержимое поля email
     * электронная почта не может быть пустой и должна содержать символ @
     * Проверяется содержит ли поле адреса почты адрес и знак @. Если нет то выбрасывается исключение и тест проходит.
     */
    @Test
    void addUserWithBlankEmailOrWithoutEmailSymbolTest() {
        //Подготовка
        User userWithoutEmail = User.builder()
                .id(1)
                .name("User")
                .login("UserLogin")
                .birthday(LocalDate.of(2020, 03, 26))
                .email("")  //без почтового адреса
                .build();

        User userWithoutEmailSymbol = User.builder()
                .id(1)
                .name("User")
                .login("UserLogin")
                .birthday(LocalDate.of(2020, 03, 26))
                .email("useryandex.ru")
                .build();

        //Действия
        ValidationException exceptionWithoutEmail = assertThrows(ValidationException.class, () -> userController.addUser(userWithoutEmail));
        ValidationException exceptionWithoutEmailSymbol = assertThrows(ValidationException.class, () -> userController.addUser(userWithoutEmailSymbol));

        //Проверка
        assertTrue(exceptionWithoutEmail.getMessage().contains("Поле электронной почты пусто или не содержит знак @"));
        assertTrue(exceptionWithoutEmailSymbol.getMessage().contains("Поле электронной почты пусто или не содержит знак @"));
    }

    /**
     * Проверка работы ограничений на содержимое поле Login
     * логин не может быть пустым и содержать пробелы;
     */
    @Test
    void addUserWithoutLoginOrLoginHasSpacesTest() {
        //Подготовка
        User userWithoutLogin = User.builder()
                .id(1)
                .name("User")
                .login("")      //Логин пустой
                .birthday(LocalDate.of(2020, 03, 26))
                .email("user@yandex.ru")
                .build();

        User userLoginWithSpaces = User.builder()
                .id(2)
                .name("User")
                .login("User Login")        //Логин содержит пробелы
                .birthday(LocalDate.of(2020, 03, 26))
                .email("user@yandex.ru")
                .build();

        //Действия
        ValidationException exceptionWithoutEmail = assertThrows(ValidationException.class,
                () -> userController.addUser(userWithoutLogin));
        ValidationException exceptionWithoutEmailSymbol = assertThrows(ValidationException.class,
                () -> userController.addUser(userLoginWithSpaces));

        //Проверка
        assertTrue(exceptionWithoutEmail.getMessage().contains("Поле логин пусто или содержит пробелы"));
        assertTrue(exceptionWithoutEmailSymbol.getMessage().contains("Поле логин пусто или содержит пробелы"));
    }

    /**
     * Проверяется установка поля name если в нём отсутсвует значение
     */
    @Test
    void addUserWhenNameIsBlankSetItToLoginTest() {
        //Подготовка
        User userWithoutName = User.builder()
                .id(1)
                .name("")           //Пустое поле
                .login("UserLogin")
                .birthday(LocalDate.of(2020, 03, 26))
                .email("user@yandex.ru")
                .build();
        String expectedName = userWithoutName.getLogin();

        //Действия
        userController.addUser(userWithoutName);
        String actualName = userController.getUsers().get(0).getName();

        //Проверка
        assertEquals(expectedName, actualName);
    }

    /**
     * Проверяется что дата дня рождения не может быть в будущем
     */
    @Test
    public void addUserBirthdayTest(){
        //Подготовка
        User userWithWrongBirthday = User.builder()
                .id(1)
                .name("Name")           //Пустое поле
                .login("UserLogin")
                .birthday(LocalDate.now().plusDays(1))
                .email("user@yandex.ru")
                .build();
        //Действия
        ValidationException validationBirthday = assertThrows(ValidationException.class, ()-> userController.addUser(userWithWrongBirthday));

        //Проверка
        assertTrue(validationBirthday.getMessage().contains("Дата дня рождения не может быть в будущем"));
    }

    /**
     * Проверяется обновление пользователя. Обновляется только если пользователь с таким идентификатором уже есть в группе.
     */
    @Test
    void updateUserTest() {
        //Подготовка
        User user1 = User.builder()
                .id(1)
                .name("Name")
                .login("UserLogin")
                .birthday(LocalDate.EPOCH)
                .email("user@yandex.ru")
                .build();

        User updatedUser = User.builder()
                .id(1)
                .name("Updated Name")
                .login("UserLogin")
                .birthday(LocalDate.EPOCH)
                .email("user@yandex.ru")
                .build();

        userController.addUser(user1);
        User expectedUser = updatedUser;

        //Действие
        userController.updateUser(updatedUser);
        User actualUser = userController.getUsers().get(0);

        //Проверка
        assertEquals(expectedUser, actualUser);
    }

    /**
     * Проверяется получение всех пользователей
     */
    @Test
    void getUsersTest() {
        //Подготовка
        User user1 = User.builder()
                .id(1)
                .name("Name")
                .login("UserLogin")
                .birthday(LocalDate.EPOCH)
                .email("user@yandex.ru")
                .build();
        User user2 = User.builder()
                .id(2)
                .name("Name")
                .login("UserLogin")
                .birthday(LocalDate.EPOCH)
                .email("user@yandex.ru")
                .build();
        userController.addUser(user1);
        userController.addUser(user2);

        int expectedSizeOfGroup = 2;

        //Действия
        int actualSizeOfGroup= userController.getUsers().size();

        //Проверка
        assertEquals(expectedSizeOfGroup, actualSizeOfGroup);
    }
}