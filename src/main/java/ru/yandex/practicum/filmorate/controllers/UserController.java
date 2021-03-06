package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    //Группа всех пользователей
    private Map<Integer, User> users = new HashMap<>();

    //Счётчик содаржит последний выданный идентификатор
    private  int idCount = 1;

    /**
     * Генератор идентификаторов
     * @return
     */
    private int idGenerator() {
        return idCount++;
    }

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
        if (someUser.getName().isBlank()){
            return someUser.toBuilder().name(someUser.getLogin()).build();
        }
        return someUser;
    } ;

    /**
     * Создаёт и проводит валидацию экземпляра класса User, а также добавляет его в группу
     */
    @PostMapping()
    public User addUser(@RequestBody User user) {
        //Присвоить фильму идентификатор
        user = user.toBuilder().id(idGenerator()).build();

        //Произвести валидацию пользователя и добавить его в группу,
        // если валидация не пройдена залогировать и выбросить исключение
        try {
            //Проверить поля на соответствие требованиям
            user.validate(validationFields);
            //Проверить поле имени. Если оно не заполнено, то установить значение из поля логин
            user = user.validate(changeNameIfBlank);
            users.put(user.getId(), user);
            log.info("В группу пользователей добавлен пользователь: {}", user.getLogin());
            return user;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Обновляет экземпляр класса User
     * @param user
     * @return
     */
    @PutMapping()
    public User updateUser(@RequestBody User user) {
        //Есть ли обновляемые пользователь в группе? Есил есть обновить, если нет залогировать и вернуть null.
        if (!users.containsKey(user.getId())) {
            log.info("Пользователя {} нет в группе.", user);
            throw new ValidationException("Ошибка обновления пользователя. Такого пользователя нет в группе.");
        }
        //Произвести валидацию пользователя и обновить его данные, если валидация не пройдена залогировать
        try {
            user.validate(validationFields);
            users.put(user.getId(), user);
            log.info("Данные пользователя {} обновлены .", user.getLogin());
            return user;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Получает группу пользователей
     * @return
     */
    @GetMapping()
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
