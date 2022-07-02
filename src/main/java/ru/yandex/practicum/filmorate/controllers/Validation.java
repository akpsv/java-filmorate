package ru.yandex.practicum.filmorate.controllers;
@FunctionalInterface
public interface Validation<T> {
    boolean test(T t) throws ValidationException;
}
