package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.controllers.Validation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.function.Function;

@Value
@Builder(toBuilder = true)
public class User {
    //идентификатор
    private int id;
    //Электронная почта
    @NotBlank
    @Email
    private String email;
    //Логин пользователя
    @NotBlank
    private String login;
    //Имя для отображения
    @NotNull
    private String name;
    //Дата рождения
    @PastOrPresent
    private LocalDate birthday;

    public User validate(Validation<User, User> validationFunction) {
        return validationFunction.validate(this);
    }
}
