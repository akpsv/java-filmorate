package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.controllers.Validation;

import java.time.LocalDate;
import java.util.function.Function;

@Value
@Builder(toBuilder = true)
public class User {
    //идентификатор
    private int id;
    //Электронная почта
    private String email;
    //Логин пользователя
    private String login;
    //Имя для отображения
    private String name;
    //Дата рождения
    private LocalDate birthday;

    public User validate(Validation<User, User> validationFunction) {
        return validationFunction.validate(this);
    }
}
