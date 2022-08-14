package ru.yandex.practicum.filmorate.dao_impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storages.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<List<Mpa>> getMpas() {
        String sqlSelectMpas = "SELECT * FROM mpas";
        List<Mpa> mpas = jdbcTemplate.query(sqlSelectMpas, this::mapRowToMpa);
        return Optional.of(mpas);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        int mpa_id = resultSet.getInt("mpa_id");
        String mpa_name = resultSet.getString("mpa_name");

        return new Mpa(mpa_id, mpa_name);
    }
}
