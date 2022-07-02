package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.controllers.Validation;

import java.time.LocalDate;

@Value()
@Builder(toBuilder = true)
public class Film {
    //Идентификатор
    private int id;
    //Название
    private String name;
    //Описание
    private String description;
    //Дата выпуска
    private LocalDate releaseDate;
    //Продолжительность фильма
    private int duration;

    public Film validate(Validation<Film, Film> validationFunction){
        return validationFunction.validate(this);
    }
}



