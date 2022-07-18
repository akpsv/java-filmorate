package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.controllers.Validation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.Set;

@Value()
@Builder(toBuilder = true)
public class Film {
    //Идентификатор
    private Long id;
    //Название
    @NotBlank
    private String name;
    //Описание
    private String description;
    //Дата выпуска
    @NotBlank
    @PastOrPresent
    private LocalDate releaseDate;
    //Продолжительность фильма
    @NotBlank
    @PositiveOrZero
    private int duration;


    //Содержит идентификаторы пользователей поставивших лайк фильму
    Set<Long> likes;

    /**
     * Принмает функцию выполняющую валидацию
     */
    public Film validate(Validation<Film, Film> validationFunction){
        return validationFunction.validate(this);
    }
}



