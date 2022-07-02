package ru.yandex.practicum.filmorate.controllers;

public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
