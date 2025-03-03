package com.javarush.lesson12.shmibernate;

import com.javarush.khmelov.entity.AbstractEntity;
import com.javarush.khmelov.repository.Repository;
import com.javarush.lesson12.shmibernate.engine.PostgresDialect;
import com.javarush.lesson12.shmibernate.engine.UniversalRepository;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class PostgresRepository {

    private static final Map<Object, Object> repositories = new HashMap<>();

    public <T extends AbstractEntity> Repository<T> get(Class<T> type) {
        //noinspection unchecked
        return (Repository<T>) repositories
                .computeIfAbsent(type, t -> new UniversalRepository<>(type, new PostgresDialect()));
    }


}
