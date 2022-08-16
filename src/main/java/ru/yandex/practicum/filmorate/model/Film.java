package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.controllers.Validation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private int rate;

    private Mpa mpa;

    private List<Genre> genres;
    //Содержит идентификаторы пользователей поставивших лайк фильму
    private Set<Long> likes;

    /**
     * Принмает функцию выполняющую валидацию
     */
    public Film validate(Validation<Film, Film> validationFunction) {
        return validationFunction.validate(this);
    }

    /**
     * Сопосталвение данных для использования в FilmDbStorage
     *
     * @return
     */
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration_min", duration);
        values.put("rate", rate);
        values.put("mpa", mpa.getId());

        return values;
    }
}



