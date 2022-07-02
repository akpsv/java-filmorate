package ru.yandex.practicum.filmorate.controllers;

import java.time.format.DateTimeParseException;

public interface Converter<T, R> {
    R convert(T t) throws DateTimeParseException;
}
