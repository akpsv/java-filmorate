package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storages.MpaStorage;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Optional<List<Mpa>> getMpas(){
        return mpaStorage.getMpas();
    }

    public Optional<Mpa> getMpaById(int id) {
        if (id<1){
            return Optional.empty();
        }
        return mpaStorage.getMpas().get().stream()
                .filter(mpa -> mpa.getId()==id)
                .findAny();
    }
}
