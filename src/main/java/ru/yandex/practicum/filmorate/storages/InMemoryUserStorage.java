package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.Validation;
import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    //Группа всех пользователей
    private Map<Long, User> users = new HashMap<>();

    //Счётчик содаржит последний выданный идентификатор
    private long idCount = 1;
    /**
     * Функция производящая валидацию полей объекта
     */
    private Validation<User, User> validationFields = (someUser) -> {
        if (someUser.getEmail().isBlank() || !someUser.getEmail().contains("@")) {
            throw new ValidationException("Ошибка. Поле электронной почты пусто или не содержит знак @.");
        }
        if (someUser.getLogin().isBlank() || someUser.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка. Поле логин пусто или содержит пробелы.");
        }
        if (someUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка. Дата дня рождения не может быть в будущем.");
        }
        return someUser;
    };
    /**
     * Функция проверки поля имени и если оно пусто то установки в него значения из поля логин
     */
    private Validation<User, User> changeNameIfBlank = (someUser) -> {
        if (someUser.getName().isBlank()) {
            return someUser.toBuilder().name(someUser.getLogin()).build();
        }
        return someUser;
    };

    /**
     * Генератор идентификаторов
     *
     * @return
     */
    private long idGenerator() {
        return idCount++;
    }

    /**
     * Создаёт и проводит валидацию экземпляра класса User, а также добавляет его в группу
     */
    @Override
    public Optional<User> addUser(User user) {
        //Произвести валидацию пользователя и добавить его в группу,
        // если валидация не пройдена залогировать и выбросить исключение
        try {
            //Присвоить пользователю идентификатор
            user = user.toBuilder().id(idGenerator()).friends(new HashSet<>()).build();
            users.put(user.getId(), user);
            log.info("В группу пользователей добавлен пользователь: {}", user.getLogin());
            return Optional.of(user);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Обновляет экземпляр класса User
     *
     * @param user
     * @return
     */
    @Override
    public Optional<User> updateUser(User user) {
        //Есть ли обновляемые пользователь в группе? Есил есть обновить, если нет залогировать и вернуть null.
        if (!users.containsKey(user.getId())) {
            log.info("Пользователя {} нет в группе.", user);
            return Optional.empty();
        }
        //Произвести валидацию пользователя и обновить его данные, если валидация не пройдена залогировать
        try {
            user.validate(validationFields);
            users.put(user.getId(), user);
            log.info("Данные пользователя {} обновлены .", user.getLogin());
            return Optional.of(user);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Получает группу пользователей
     *
     * @return
     */
    @Override
    public Optional<List<User>> getUsers() {
        return Optional.of(new ArrayList<>(users.values()));
    }
}
