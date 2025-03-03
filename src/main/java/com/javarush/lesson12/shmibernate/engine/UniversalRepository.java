package com.javarush.lesson12.shmibernate.engine;

import com.javarush.khmelov.entity.AbstractEntity;
import com.javarush.khmelov.repository.Repository;
import com.javarush.lesson08.CnnPool;
import jakarta.persistence.*;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

public class UniversalRepository<T extends AbstractEntity> implements Repository<T> {

    private final Class<T> type;
    private final Dialect dialect;

    //fieldsWithFirstId
    private final List<Field> fields;
    private final String tableName;

    public UniversalRepository(Class<T> type, Dialect dialect) {
        this.type = type;
        this.dialect = dialect;
        if (type.isAnnotationPresent(Entity.class)) {
            fields = Arrays.stream(type.getDeclaredFields())
                    .sorted(Comparator.comparingInt(f -> (f.isAnnotationPresent(Id.class) ? 0 : 1)))
                    .filter(f -> !f.isAnnotationPresent(Transient.class))
                    .filter(f -> !f.isAnnotationPresent(ManyToOne.class))
                    .filter(f -> !f.isAnnotationPresent(OneToMany.class))
                    .filter(f -> !f.isAnnotationPresent(OneToOne.class))
                    .filter(f -> !f.isAnnotationPresent(ManyToMany.class))
                    .toList();
            tableName = StrategyNaming.getTableName(type);
            createTableIfNotExists();
        } else {
            throw new RuntimeException("incorrect class");
        }
    }

    private static boolean containsValue(Field f, Object entity) {
        f.setAccessible(true);
        try {
            return f.get(entity) != null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public Collection<T> getAll() {
        String sql = dialect.getGetAllSql(tableName, fields);
        System.out.println(sql);
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            return readRows(resultSet).toList();
        }
    }

    @SneakyThrows
    @Override
    public Stream<T> find(T entity) {
        List<Field> whereField = fields.stream()
                .filter(f -> containsValue(f, entity))
                .toList();
        String findSql = dialect.getFindSql(tableName, fields, whereField);
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(findSql)) {
            fill(entity, preparedStatement, whereField);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            return readRows(resultSet);
        }
    }

    @Override
    @SneakyThrows
    public T get(long id) {
        String sql = dialect.getGetByIdSql(tableName, fields);
        System.out.println(sql);
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            T entity = type.getConstructor().newInstance();
            entity.setId(id);
            setId(entity, preparedStatement, 1);
            ResultSet resultSet = preparedStatement.executeQuery();
            return readRows(resultSet).findFirst().orElse(null);
        }
    }

    @Override
    @SneakyThrows
    public void create(T entity) {
        String sql = dialect.getCreateSql(tableName, fields);
        System.out.println(sql);
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(entity, preparedStatement);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                entity.setId(id);
            } else {
                throw new RuntimeException("error save " + entity);
            }
        }
    }

    @SneakyThrows
    @Override
    public void update(T entity) {
        String sql = dialect.getUpdateSql(tableName, fields);
        System.out.println(sql);
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            fill(entity, preparedStatement);
            setId(entity, preparedStatement, fields.size());
            preparedStatement.executeUpdate();
        }
    }


    @SneakyThrows
    @Override
    public void delete(T entity) {
        String sql = dialect.getDeleteSql(tableName, fields);
        try (Connection connection = CnnPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setId(entity, preparedStatement, 1);
            preparedStatement.executeUpdate();
        }
    }

    @SneakyThrows
    private void createTableIfNotExists() {
        String sql = dialect.getCreateTableSql(tableName, fields);
        System.out.println(sql);
        try (Connection connection = CnnPool.get();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private Object getValue(T entity, Field field) {
        field.setAccessible(true);
        try {
            Object value = field.get(entity);
            if (field.getType().isEnum()) {
                value = value.toString();
            }
            return value;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void fill(T entity, PreparedStatement preparedStatement) {
        List<Field> dataFields = fields.subList(1, fields.size());
        fill(entity, preparedStatement, dataFields);
    }

    @SneakyThrows
    private void fill(T entity, PreparedStatement preparedStatement, List<Field> customFields) {
        Stream.iterate(0, i -> ++i)
                .limit(customFields.size())
                .forEach(i -> {
                            Field field = customFields.get(i);
                            field.setAccessible(true);
                            try {
                                Object value = getValue(entity, field);
                                preparedStatement.setObject(i + 1, value);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    private void setId(T entity, PreparedStatement preparedStatement, int pos) {
        try {
            Field idField = fields.get(0);
            Object value = getValue(entity, idField);
            preparedStatement.setObject(pos, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private Stream<T> readRows(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T entity = type.getConstructor().newInstance();
            for (Field field : fields) {
                String fieldName = StrategyNaming.getFieldName(field);
                String valueString = resultSet.getString(fieldName);
                dialect.read(entity, field, valueString);
            }
            //addProxies(entity);
            list.add(entity);
        }
        return list.stream();
    }

}
