package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    //Группа всех пользователей
    private Map<Integer, User> users = new HashMap<>();

    //Функция производящая валидацию полей объекта
    private Validation<User> validationFields = (someUser) -> {
        if (someUser.getEmail().isBlank() || !someUser.getEmail().contains("@")) {
            throw new ValidationException("Ошибка. Поле электронной почты пусто или не содержит знак @.");
        }
        if (someUser.getLogin().isBlank() || someUser.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка. Поле логин пусто или содержит пробелы.");
        }
        if (someUser.getName().isBlank()) {
            someUser.toBuilder().name(someUser.getLogin());
        }
        if (someUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка. Дата дня рождения не может быть в будущем.");
        }
        return true;
    };

    @PostMapping()
    public User addUser(@RequestBody User user) {
        //Произвести валидацию пользователя и добавить его в группу, если валидация не пройдена залогировать
        try {
            user.validate(validationFields);
            if (users.containsKey(user.getId())) {
                log.info("Пользователь {} уже находится в группе.", user.getLogin());
            } else {
                users.put(user.getId(), user);
                log.info("В группу пользователей добавлен пользователь: {}", user.getLogin());
            }
            return user;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
        }
        //Возвращается если пользователь не прошёл валидацию
        return null;
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        //Есть ли обновляемые пользователь в группе? Есил есть обновить, если нет залогировать и вернуть null.
        if (!users.containsKey(user.getId())) {
            log.info("Пользователя {} нет в группе.", user);
            return null;
        }
        //Произвести валидацию пользователя и обновить его данные, если валидация не пройдена залогировать
        try {
            user.validate(validationFields);
            users.put(user.getId(), user);
            log.info("Данные пользователя {} обновлены .", user.getLogin());
            return user;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
        }
        //Возвращается если пользователь не прошёл валидацию
        return null;
    }

    @GetMapping()
    public List<User> getUsers() {
        throw new NullPointerException("Don't exist getUsers method");
    }
}
