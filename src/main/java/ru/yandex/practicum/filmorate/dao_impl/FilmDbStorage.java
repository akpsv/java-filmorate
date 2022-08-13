package ru.yandex.practicum.filmorate.dao_impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

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
        if (film.getGenres()!=null) {
            String sqlInsertGenres = "INSERT INTO films_genres(film_id, genre_id) VALUES(?, ?)";
            film.getGenres().stream()
                    .forEach(entry -> jdbcTemplate.update(sqlInsertGenres,
                            filmId,
                            entry.get("id")));
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

        String sqlUpdateUser = "UPDATE films SET film_name=?, description=?, release_date=?, duration_min=?, rate = ?, mpa = ?  WHERE film_id = ?";
        int update = jdbcTemplate.update(sqlUpdateUser,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().get("id"),
                film.getId()
        );

        if (update==1){
            return Optional.of(film);
        }else {
            return Optional.empty();
        }
    }

    /**
     * Обновить список идентификаторов друзей идентификатором нового друга
     * @param film - пользователь к которому добавляем друга
     * @return true - если друг добалвен, иначе - false
     */
    private boolean updateLikes(Film film){
        if (film.getLikes()==null || film.getLikes().isEmpty()){
            return false;
        }
        //Получить лайки фильма находящиеся в БД
        String sqlSelectLikes = "SELECT user_id FROM likes  WHERE film_id = ?";
        List<Long> likesFromDB = jdbcTemplate.query(sqlSelectLikes,
                (rs, rowNum) -> rs.getLong("film_id"),
                film.getId());
        //Если лайков в объекте Фильм больше чем в БД , то добавить лайк иначе удалить
        if (film.getLikes().size()> likesFromDB.size()) {
            return addLikeToFilm(film, likesFromDB);
        } else if (film.getLikes().size()< likesFromDB.size()) {
            return deleteLikeFromFilm(film, likesFromDB);
        } else {
            return false;
        }
    }

    /**
     * Добавить лайк фильму
     * @param film
     * @param likesFromDB
     * @return
     */
    private boolean addLikeToFilm(Film film, List<Long> likesFromDB){
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
     * @param film
     * @param likesFromDB
     * @return
     */
    private boolean deleteLikeFromFilm(Film film, List<Long> likesFromDB){
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

    private Map<String, Integer> getMpaForFilm(ResultSet resultSet) throws SQLException {
        Map<String, Integer> mpa = new HashMap<>();
        int mpaIdFromFilm = resultSet.getInt("mpa");
        //Получить название mpa
        String sqlSelectMpaName = "SELECT mpa_name FROM mpas WHERE mpa_id = ?";
        String mpa_name = jdbcTemplate.query(sqlSelectMpaName, (rs, rowNum) -> rs.getString("mpa_name"), mpaIdFromFilm).get(0);
        mpa.put("id", mpaIdFromFilm);
        mpa.put("name", mpa_name);
        return mpa;
    }

    //TODO: доделать
    private List<Map<String, Integer>> getGenresForFilm(long filmId) throws SQLException {
        Map<String, Integer> mapGenres = new HashMap<>();
        String sqlSelectGenres = "SELECT g.genre_id FROM films_genres AS fg  INNER JOIN genres AS g  ON fg.genre_id = g.genre_id  WHERE fg.film_id = ?";
        List<Integer> genresId = jdbcTemplate.query(sqlSelectGenres, (rs, rowNum) -> rs.getInt("genre_id"), filmId);
        List<Map<String, Integer>> genres = new ArrayList<>();
        if (genresId.size()==0){
            return genres;
        }
        genresId.stream()
                .forEach(genreId -> mapGenres.put("id", genreId) );
        genres.add(mapGenres);
//Где то в методе надо поставить проверку , чтобы не добавлять в итоговый список пустое отображение иначе список состоит из одного элемента но пустого
        return genres;
    }
}
