package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.controllers.Validation;

import java.time.LocalDate;

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

    public boolean validate(Validation validationFunction) {
        return validationFunction.test(this);
    }

    public User update(User changedUser) {
        this.toBuilder()
                .email(changedUser.email)
                .login(changedUser.login)
                .name(changedUser.name)
                .birthday(changedUser.birthday)
                .build();
        return this;
    }

}
