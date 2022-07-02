package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.controllers.Converter;
import ru.yandex.practicum.filmorate.controllers.Validation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
    private String releaseDate;
    //Продолжительность фильма
    private int duration;

    public boolean validate(Validation validationFunction){
        return validationFunction.test(this);
    }

    public LocalDate convertToLocalDate(Converter<String, LocalDate> convertFunction) throws DateTimeParseException {
        return convertFunction.convert(releaseDate);
    }
}



