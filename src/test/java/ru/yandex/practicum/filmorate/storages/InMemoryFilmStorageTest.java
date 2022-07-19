package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storages.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
    //Переменная содержит экземпляр проверяемого класса
    private InMemoryFilmStorage inMemoryFilmStorage;

    /**
     * Создание экземпляра класса
     */
    @BeforeEach
    void setUp() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
    }

    /**
     * Проверяет добавление фильма в группу фильмов
     */
    @Test
    void addFilmTest() {
        //Подготовка
        Film film1 = Film.builder()
                .id(1L)
                .name("Film 1")
                .description("Some film")
                .releaseDate(LocalDate.of(2000, 03, 26))
                .duration(60)
                .build();

        int expectedSizeOfGroup = 1;

        //Действия
        //Передаётся правильно сформированный объект
        inMemoryFilmStorage.addFilm(film1);
        int actualSizeOfGroup = inMemoryFilmStorage.getFilms().get().size();
        //Проверка
        assertEquals(expectedSizeOfGroup, actualSizeOfGroup);

        //Проверка работы валидации
        //1. Навзание не может быть пустым
        //Подготовка
        Film filmWithoutName = Film.builder()
                .id(10L)
                .name("")                   //Пустое название
                .description("Some film")
                .releaseDate(LocalDate.of(2000, 03, 26))
                .duration(60)
                .build();
        //Действие
        ValidationException validationException = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.addFilm(filmWithoutName));
        //Проверка
        assertTrue(validationException.getMessage().contains("Название не заполнено"));

        //2. Максимальная длина описания — 200 символов
        //Проверяется выбрасывание исключения при длинне описания в 201 символ
        //Подготовка
        String text201 = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor." +
                " Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec qua";

        Film filmWithLengthMore200 = Film.builder()
                .id(11L)
                .name("Film 11")
                .description(text201)       //В описании 201 символ
                .releaseDate(LocalDate.of(2000, 03, 26))
                .duration(60)
                .build();

        //Действие
        ValidationException validationException201 = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.addFilm(filmWithLengthMore200));
        //Проверка
        assertTrue(validationException201.getMessage().contains("Длинна описания больше 200 символов"));
    }

    /**
     * Проверяется успешное добавление фильма при длине описания фильма в 200 символов
     */
    @Test
    void addFilmWithLengthOfName200Test() {
        //Подготовка
        String text200 = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor." +
                " Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec qu";
        Film filmWithLength200 = Film.builder()
                .id(11L)
                .name("Film 11")
                .description(text200) //в описании ровно 200 символов
                .releaseDate(LocalDate.of(2000, 03, 26))
                .duration(60)
                .build();

        int expectedSizeOfGroup = 1;
        //Действия
        inMemoryFilmStorage.addFilm(filmWithLength200);
        int actualSizeOfGroup = inMemoryFilmStorage.getFilms().get().size();

        //Проверка
        assertEquals(expectedSizeOfGroup, actualSizeOfGroup);
    }

    /**
     * Проверяется выбрасывание исключения при неправильной дате релиза фильма
     */
    @Test
    void addFilmWithWrongDateTest() {
        //Подготовка
        Film filmWithWrongDate = Film.builder()
                .id(11L)
                .name("Film 11")
                .description("Some film")
                .releaseDate(LocalDate.of(1895,12,27)) //Неверная дата
                .duration(60)
                .build();

        //Действия
        ValidationException validationException = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.addFilm(filmWithWrongDate));

        //Проверка
        assertTrue(validationException.getMessage().contains("Дата релиза фильма не соответствует реальности"));
    }

    /**
     * Проверяется выбрасывание исключения при неправильной продолжительности фильма
     */
    @Test
    void addFilmWithWrongDurationTest() {
        //Подготовка
        Film filmWithWrongDuration = Film.builder()
                .id(11L)
                .name("Film 11")
                .description("Some film")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(0) //Неправильная продолжительность
                .build();

        //Действия
        ValidationException validationException = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.addFilm(filmWithWrongDuration));

        //Проверка
        assertTrue(validationException.getMessage().contains("Продолжительность фильма меньше или равна нулю"));
    }

    /**
     * Проверяется получение всех фильмов из коллекции
     */
    @Test
    void getFilmsTest() {
        //Подготовка
        Film film1 = Film.builder()
                .id(1L)
                .name("Film 1")
                .description("Some film")
                .releaseDate(LocalDate.of(2000, 03, 26))
                .duration(60)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .name("Film 1")
                .description("Some film")
                .releaseDate(LocalDate.of(2000, 03, 26))
                .duration(60)
                .build();
        inMemoryFilmStorage.addFilm(film1);
        inMemoryFilmStorage.addFilm(film2);

        int expectedSizeOfGroup = 2;

        //Действия
        int actualSizeOfGroup = inMemoryFilmStorage.getFilms().get().size();

        //Проверка
        assertEquals(expectedSizeOfGroup, actualSizeOfGroup);
    }


}