package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    /**
     * Проверяет добавление фильма в группу фильмов
     */
    @Test
    void addFilmTest() {
        //Подготовка
        Film film1 = Film.builder()
                .id(1)
                .name("Film 1")
                .description("Some film")
                .releaseDate("2000-03-26")
                .duration(60)
                .build();

        int expectedSizeOfGroup = 1;

        //Действия
        //Передаётся правильно сформированный объект
        filmController.addFilm(film1);
        int actualSizeOfGroup = filmController.getFilms().size();
        //Проверка
        assertEquals(expectedSizeOfGroup, actualSizeOfGroup);

        //Проверка передачи объекта с не правильно заполненным полем releaseDate
        //Подготовка
        Film wrongFilm = Film.builder()
                .id(5)
                .name("Film 5")
                .description("Some film")
                .releaseDate("2020.03.26") //Дата в не правильном формате
                .duration(60)
                .build();
        //Действие
        //Передаётся дата в поле releaseDate объекта film в не правильном формате
        DateTimeParseException parseException = assertThrows(DateTimeParseException.class, () -> filmController.addFilm(wrongFilm));
        //Проверка
        assertTrue(parseException.getMessage().contains("could not be parsed"));

        //Проверка работы валидации
        //1. Навзание не может быть пустым
        //Подготовка
        Film filmWithoutName = Film.builder()
                .id(10)
                .name("")                   //Пустое название
                .description("Some film")
                .releaseDate("2020-03-26")
                .duration(60)
                .build();
        //Действие
        ValidationException validationException = assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithoutName));
        //Проверка
        assertTrue(validationException.getMessage().contains("Название не заполнено"));

        //2. Максимальная длина описания — 200 символов
        //Проверяется выбрасывание исключения при длинне описания в 201 символ
        //Подготовка
        String text201 = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor." +
                " Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec qua";

        Film filmWithLengthMore200 = Film.builder()
                .id(11)
                .name("Film 11")
                .description(text201)       //В описании 201 символ
                .releaseDate("2020-03-26")
                .duration(60)
                .build();

        //Действие
        ValidationException validationException201 = assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithLengthMore200));
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
                .id(11)
                .name("Film 11")
                .description(text200) //в описании ровно 200 символов
                .releaseDate("2020-03-26")
                .duration(60)
                .build();

        int expectedSizeOfGroup = 1;
        //Действия
        filmController.addFilm(filmWithLength200);
        int actualSizeOfGroup = filmController.getFilms().size();

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
                .id(11)
                .name("Film 11")
                .description("Some film")
                .releaseDate("1895-12-27") //Неверная дата
                .duration(60)
                .build();

        //Действия
        ValidationException validationException = assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithWrongDate));

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
                .id(11)
                .name("Film 11")
                .description("Some film")
                .releaseDate("1895-12-28")
                .duration(0) //Неправильная продолжительность
                .build();

        //Действия
        ValidationException validationException = assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithWrongDuration));

        //Проверка
        assertTrue(validationException.getMessage().contains("Продолжительность фильма меньше или равна нулю"));
    }

    /**
     * Проверяется выполнение обновления фильма в коллекции
     */
    @Test
    void updateFilmTest() {
        //Подготовка
        Film firstFilm = Film.builder()
                .id(8)
                .name("Film 1")
                .description("Some film")
                .releaseDate("2000-03-26")
                .duration(60)
                .build();
        //Обновлённый фильм
        Film expectedFilm = firstFilm.toBuilder()
                .name("Updated Film 1")
                .build();
        //Добавление первого фильма в коллекцию
        filmController.addFilm(firstFilm);

        //Действия
        Film actualFilm = filmController.updateFilm(expectedFilm);

        //Проверка
        assertEquals(expectedFilm, actualFilm);

        //Проверка отказа в обновлении при передаче фильма которого нет в коллекции
        //Подготовка
        Film film2 = Film.builder()
                .id(2)
                .name("Film 1")
                .description("Some film")
                .releaseDate("2000-03-26")
                .duration(60)
                .build();
        //Действие
        ValidationException validationException = assertThrows(ValidationException.class, ()-> filmController.updateFilm(film2));
        //Проверка
        assertTrue(validationException.getMessage().contains("Ошибка обновления фильма"));
    }

    /**
     * Проверяется получение всех фильмов из коллекции
     */
    @Test
    void getFilmsTest() {
        //Подготовка
        Film film1 = Film.builder()
                .id(1)
                .name("Film 1")
                .description("Some film")
                .releaseDate("2000-03-26")
                .duration(60)
                .build();
        Film film2 = Film.builder()
                .id(2)
                .name("Film 1")
                .description("Some film")
                .releaseDate("2000-03-26")
                .duration(60)
                .build();
        filmController.addFilm(film1);
        filmController.addFilm(film2);

        int expectedSizeOfGroup = 2;

        //Действия
        int actualSizeOfGroup = filmController.getFilms().size();

        //Проверка
        assertEquals(expectedSizeOfGroup, actualSizeOfGroup);
    }

    /**
     * Проверяется правильность конвертации даты из строки в определённом формате в  тип LocalDate
     */
    @Test
    void convertToLocalDateTest() {
        //Подготовка
        Film film1 = Film.builder()
                .id(1)
                .name("Film 1")
                .description("Some film")
                .releaseDate("2000-03-26")
                .duration(60)
                .build();

        LocalDate expectedLocalDate = LocalDate.of(2000, 03, 26);
        //Функция преобразования строки определённого формата в тип LocalDate
        Converter<String, LocalDate> convertToLocalDateFunc = (string) -> LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Действия
        LocalDate actualLocalDate = film1.convertToLocalDate(convertToLocalDateFunc);

        //Проверка
        assertEquals(expectedLocalDate, actualLocalDate);
    }

}