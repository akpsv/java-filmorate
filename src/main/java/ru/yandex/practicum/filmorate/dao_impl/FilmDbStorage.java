package ru.yandex.practicum.filmorate.dao_impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> addFilm(Film film) {
        SimpleJdbcInsert insertFilmData = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long filmId = insertFilmData.executeAndReturnKey(film.toMap()).longValue();
        //Сохранить жанры
        if (film.getGenres() != null) {
            String sqlInsertGenres = "INSERT INTO films_genres(film_id, genre_id) VALUES(?, ?)";
            film.getGenres().stream()
                    .forEach(genre -> jdbcTemplate.update(sqlInsertGenres,
                            filmId,
                            genre.getId()));
        }
        //Сохранить лайки
        //Получить и вернуть сохранённый фильм из базы
        String sqlSelectSavedFilm = "SELECT * FROM films WHERE film_id = ?";
        Film sevedFilmFromDb = jdbcTemplate.queryForObject(sqlSelectSavedFilm, this::mapRowToFilm, filmId);

        return Optional.of(sevedFilmFromDb);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        //Обновить лайки
        updateLikes(film);
        updateGenres(film);

        String sqlUpdateUser = "UPDATE films SET film_name=?, description=?, release_date=?, duration_min=?, rate = ?, mpa = ?  WHERE film_id = ?";
        int update = jdbcTemplate.update(sqlUpdateUser,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId()
        );

        //Получить обновлённый фильм из БД
        String sqlSelectUpdatedFilm = "SELECT * FROM films WHERE film_id = ?";
        Film updatedFilm = jdbcTemplate.queryForObject(sqlSelectUpdatedFilm, this::mapRowToFilm, film.getId());

        //Получить обновлённые жанры из БД и добавить в фильм
        String sqlSelectGenresForFilm = "SELECT g.genre_id, g.genre_name FROM films_genres AS fg INNER JOIN genres AS g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlSelectGenresForFilm, (rs, rowNum) -> {
            return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
        }, film.getId());

        Film fullUpdatedFilm = updatedFilm.toBuilder().genres(genres).build();

        if (update == 1) {
            return Optional.of(fullUpdatedFilm);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Обновить список лайков фильма
     *
     * @param film - фильм, лайки которого обновляются
     * @return true - если лайки обновлены, иначе - false
     */
    private boolean updateLikes(Film film) {
        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            return false;
        }
        //Получить лайки фильма находящиеся в БД
        String sqlSelectLikes = "SELECT user_id FROM likes  WHERE film_id = ?";
        List<Long> likesFromDB = jdbcTemplate.query(sqlSelectLikes,
                (rs, rowNum) -> rs.getLong("film_id"),
                film.getId());
        //Если лайков в объекте Фильм больше чем в БД , то добавить лайк иначе удалить
        if (film.getLikes().size() > likesFromDB.size()) {
            return addLikeToFilm(film, likesFromDB);
        } else if (film.getLikes().size() < likesFromDB.size()) {
            return deleteLikeFromFilm(film, likesFromDB);
        } else {
            return false;
        }
    }

    /**
     * Добавить лайк фильму
     *
     * @param film
     * @param likesFromDB
     * @return
     */
    private boolean addLikeToFilm(Film film, List<Long> likesFromDB) {
        //Получить множество идентификаторов пользователей с добавленным другом
        //TODO: что-то сделать с возможным null
        Set<Long> setWithNewLike = film.getLikes();
        //Получить идентификатор добавленного друга
        setWithNewLike.removeAll(likesFromDB);
        if (setWithNewLike.isEmpty()) {
            return false;
        }
        long newLikeUserId = setWithNewLike.stream().findFirst().get();
        //Вставить идентификатор нового друга
        String sqlInsertLike = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlInsertLike, film.getId(), newLikeUserId);
        return true;
    }

    /**
     * Удалить лайк у фильма
     *
     * @param film
     * @param likesFromDB
     * @return
     */
    private boolean deleteLikeFromFilm(Film film, List<Long> likesFromDB) {
        //Получить множество лайков из которого удалён один лайк
        //TODO: что-то сделать с возможным null
        Set<Long> setWithoutLike = film.getLikes();
        //Получить идентификатор удалённого лайка
        likesFromDB.removeAll(setWithoutLike);
        long deletingLikeUserId = likesFromDB.get(0);

        //Вставить идентификатор нового друга
        String sqlDeleteLike = "DELETE FROM likes WHERE film_id = ? AND user_id= ? ";
        jdbcTemplate.update(sqlDeleteLike, film.getId(), deletingLikeUserId);
        return true;
    }


    /**
     * Обновить список жарнов фильма
     *
     * @param film - фильм, список жанров которого обновляется
     * @return true - если жанры обновлены, иначе - false
     */
    private boolean updateGenres(Film film) {
        if (film.getGenres() == null) {
            return false;
        }
        //Получить жанры фильма находящиеся в БД
        String sqlSelectGenres = "SELECT genre_id FROM films_genres  WHERE film_id = ?";
        List<Integer> genresFromDB = jdbcTemplate.query(sqlSelectGenres,
                (rs, rowNum) -> rs.getInt("genre_id"),
                film.getId());
        //Если лайков в объекте Фильм больше чем в БД , то добавить лайк иначе удалить
        List<Genre> distinctGenres = film.getGenres().stream().distinct().collect(Collectors.toList());
        if (distinctGenres.size() > genresFromDB.size()) {
            return addGenreToFilm(film, genresFromDB);
        } else if (distinctGenres.size() < genresFromDB.size()) {
            return deleteGenreFromFilm(film, genresFromDB);
        } else {
            return false;
        }

        //Просто замена все жанров в БД
        //Получить объекты жанров с добавленным жанром из фильма

//        List<Integer> setWithNewGenre = film.getGenres().stream()
//                .map(genre -> genre.getId())
//                .collect(Collectors.toList());
//        //Добавить отсутсвующие у фильм жанры
//        setWithNewGenre.stream()
//                .forEach(genreId -> {
//                    //Добавить новые жанры фильму в таблицу
//                    String sqlInsertGenre = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";
//                    jdbcTemplate.update(sqlInsertGenre, film.getId(), genreId);
//                });

    }

    /**
     * Добавить жанр фильму
     *
     * @param film
     * @param genresFromDB
     * @return
     */
    private boolean addGenreToFilm(Film film, List<Integer> genresFromDB) {
        //Получить объекты жанров с добавленным жанром из фильма
        //TODO: что-то сделать с возможным null
        List<Integer> setWithNewGenre = film.getGenres().stream()
                .map(genre -> genre.getId())
                .collect(Collectors.toList());

        //Получить жанры которые необходимо добавить
        setWithNewGenre.removeAll(genresFromDB);
        if (setWithNewGenre.isEmpty()) {
            return false;
        }
        //Добавить отсутсвующие у фильм жанры
        setWithNewGenre.stream()
                .forEach(genreId -> {
                    //Добавить новые жанры фильму в таблицу
                    String sqlInsertGenre = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";
                    jdbcTemplate.update(sqlInsertGenre, film.getId(), genreId);
                });
//        int newGenreId = setWithNewGenre.stream().findFirst().get();
        //Вставить идентификатор нового друга
//        String sqlInsertGenre = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";
//        jdbcTemplate.update(sqlInsertGenre, film.getId(), newGenreId);
        return true;
    }

    /**
     * Удалить жанр у фильма
     *
     * @param film
     * @param genresFromDB
     * @return
     */
    private boolean deleteGenreFromFilm(Film film, List<Integer> genresFromDB) {
        //Получить объекты жанров из которых удалён один жанр из фильма
        //TODO: что-то сделать с возможным null
        List<Integer> setWithoutGenre = film.getGenres().stream()
                .map(genre -> genre.getId())
                .collect(Collectors.toList());
        //Получить идентификатор удалённого лайка
        genresFromDB.removeAll(setWithoutGenre);
        long deletingGenreId = genresFromDB.get(0);

        //Вставить идентификатор нового друга
        String sqlDeleteGenre = "DELETE FROM films_genres WHERE film_id = ? AND genre_id= ? ";
        jdbcTemplate.update(sqlDeleteGenre, film.getId(), deletingGenreId);
        return true;
    }

    @Override
    public Optional<List<Film>> getFilms() {
        //Получить всех пользователей
        String sqlSelectAllFilms = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sqlSelectAllFilms, this::mapRowToFilm);

        return Optional.of(films);
    }

    /**
     * Создать объект пользователя из строки БД
     *
     * @param resultSet
     * @param rowNum
     * @return
     * @throws SQLException
     */
    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        //Получить идентификатор фильма
        long filmId = resultSet.getLong("film_id");

        //Сформировать объект пользователя
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration_min"))
                .likes(getLikesForFilm(filmId))
                .genres(getGenresForFilm(filmId))
//                .mpa(getMpaForFilm(mpaIdFromFilm))
                .mpa(getMpaForFilm(resultSet))
                .build();
    }

    private Set<Long> getLikesForFilm(long filmId) {
        String sqlSelectLikes = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sqlSelectLikes, (rs, rowNum) -> rs.getLong("user_id"), filmId);
        return likes.stream().collect(Collectors.toSet());
    }

    private Mpa getMpaForFilm(ResultSet resultSet) throws SQLException {
        Mpa mpa = new Mpa();
        int mpaIdFromFilm = resultSet.getInt("mpa");
        //Получить название mpa
        String sqlSelectMpaName = "SELECT mpa_name FROM mpas WHERE mpa_id = ?";
        String mpa_name = jdbcTemplate.query(sqlSelectMpaName, (rs, rowNum) -> rs.getString("mpa_name"), mpaIdFromFilm).get(0);
        mpa.setId(mpaIdFromFilm);
        mpa.setName(mpa_name);
        return mpa;
    }

    //TODO: доделать
    private List<Genre> getGenresForFilm(long filmId) throws SQLException {
        //Получить идентификаторы жанров фильма
        String sqlSelectGenres = "SELECT g.genre_id FROM films_genres AS fg  INNER JOIN genres AS g  ON fg.genre_id = g.genre_id  WHERE fg.film_id = ?";
        List<Integer> genresId = jdbcTemplate.query(sqlSelectGenres, (rs, rowNum) -> rs.getInt("genre_id"), filmId);

        if (genresId.size() == 0) {
            return Collections.emptyList();
        }

        List<Genre> genres =  genresId.stream()
                .map(genreId -> {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    String sqlSelectNameOfGenre = "SELECT genre_name FROM genres WHERE genre_id = ?";
                    String genreName = jdbcTemplate.query(sqlSelectNameOfGenre, (rs, rowNum) -> rs.getString("genre_name"), genreId).get(0);
                    genre.setName(genreName);
                    return genre; })
                .collect(Collectors.toList());

        return genres;
    }
}
