package ru.yandex.practicum.filmorate.controllers;

@FunctionalInterface
public interface Validation<T, R> {
    R validate(T t) throws ValidationException;
}
