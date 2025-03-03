package com.javarush.lesson12.shmibernate.engine;

import com.javarush.khmelov.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UnknownFormatConversionException;
import java.util.function.Function;
import java.util.stream.Collectors;


public class PostgresDialect implements Dialect {

    private static final String DELIMITER = ",\n";
    private static final String PREFIX = "(\n";
    private static final String SUFFIX = "\n)";
    private static final String COMMA = ", ";
    private static final String PRIMARY_KEY = "BIGSERIAL PRIMARY KEY";
    private static final String NOT_NULL = "NOT NULL ";
    private static final String UNIQUE = "UNIQUE ";
    private static final String SPACE = " ";

    private static final Map<Class<?>, String> dbType = Map.of(
            int.class, "INT",
            Integer.class, "INT",
            long.class, "INT8",
            Long.class, "INT8",
            double.class, "FLOAT8",
            Double.class, "FLOAT8",
            String.class, "TEXT"
            //..................... и т.д.
    );

    private static final Map<Class<?>, Function<String, Object>> readMap = Map.of(
            int.class, Integer::parseInt,
            Integer.class, Integer::valueOf,
            long.class, Long::parseLong,
            Long.class, Long::valueOf,
            double.class, Double::parseDouble,
            Double.class, Double::valueOf,
            String.class, String::toString
            //..................... и т.д.
    );

    private String createTableSql;
    private String getAllSql;
    private String getSql;
    private String createSql;
    private String updateSql;
    private String deleteSql;

    @Override
    public String getCreateTableSql(String tableName, List<Field> fields) {
        if (Objects.isNull(createTableSql)) {
            String fieldData = fields.stream()
                    .map(this::getOneTypeCreateTable)
                    .collect(Collectors.joining(DELIMITER, PREFIX, SUFFIX));
            createTableSql = "CREATE TABLE IF NOT EXISTS %s %s;"
                    .formatted(tableName, fieldData);
        }
        return createTableSql;
    }

    @Override
    public String getGetAllSql(String tableName, List<Field> fields) {
        if (Objects.isNull(getAllSql)) {
            String fieldNames = fields.stream()
                    .map(StrategyNaming::getFieldName)
                    .collect(Collectors.joining(COMMA));
            getAllSql = "SELECT %s\n\tFROM \"%s\";"
                    .formatted(fieldNames, tableName);
        }
        return getAllSql;
    }

    @Override
    public String getFindSql(String tableName, List<Field> fields, List<Field> whereFields) {
        String fieldNames = fields.stream()
                .map(StrategyNaming::getFieldName)
                .collect(Collectors.joining(COMMA));
        String where_ = whereFields.size() > 0
                ? whereFields.stream()
                .map(StrategyNaming::getFieldName)
                .collect(Collectors.joining("=? AND ", "WHERE ", "=?"))
                : "";
        return "SELECT %s\n\tFROM \"%s\" %s;"
                .formatted(fieldNames, tableName, where_);
    }

    @Override
    public String getGetByIdSql(String tableName, List<Field> fields) {
        if (Objects.isNull(getSql)) {
            String fieldNames = fields.stream()
                    .map(StrategyNaming::getFieldName)
                    .collect(Collectors.joining(COMMA));
            String where = StrategyNaming.getFieldName(fields.get(0)) + "=?";
            getSql = "SELECT %s\n\tFROM \"%s\"\n\tWHERE %s;"
                    .formatted(fieldNames, tableName, where);
        }
        return getSql;
    }

    @Override
    public String getCreateSql(String tableName, List<Field> fields) {
        if (Objects.isNull(createSql)) {
            String fieldNames = fields.stream()
                    .skip(1L)
                    .map(StrategyNaming::getFieldName)
                    .collect(Collectors.joining(COMMA));
            String places = fieldNames.replaceAll("[^,]+", "?");
            createSql = "INSERT INTO \"%s\" (%s)\n\tVALUES (%s)"
                    .formatted(tableName, fieldNames, places);
        }
        return createSql;
    }

    @Override
    public String getUpdateSql(String tableName, List<Field> fields) {
        if (Objects.isNull(updateSql)) {
            String setExpression = fields.stream()
                    .skip(1)
                    .map(StrategyNaming::getFieldName)
                    .map(" %s=?"::formatted)
                    .collect(Collectors.joining(COMMA));
            String where = StrategyNaming.getFieldName(fields.get(0)) + "=?";
            updateSql = "UPDATE \"%s\"\n\tSET %s \n\tWHERE %s;"
                    .formatted(tableName, setExpression, where);
        }
        return updateSql;
    }

    @Override
    public String getDeleteSql(String tableName, List<Field> fields) {
        if (Objects.isNull(deleteSql)) {
            String where = StrategyNaming.getFieldName(fields.get(0)) + "=?";
            deleteSql = "DELETE FROM \"%s\"\n\tWHERE %s;"
                    .formatted(tableName, where);
            System.out.println(deleteSql);
        }
        return deleteSql;
    }

    @Override
    public <T extends AbstractEntity> void read(T entity, Field field, String valueString) {
        Class<?> type = field.getType();
        Object value = null;
        if (!type.isEnum()) {
            value = readMap.get(type).apply(valueString);
        } else {
            for (Object enumConstant : type.getEnumConstants()) {
                if (enumConstant.toString().equalsIgnoreCase(valueString)) {
                    value = enumConstant;
                    break;
                }
            }
        }
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDatabaseType(Field field) {
        //any enum as string (but hibernate by default use INT !)
        //here we have a behavior similar annotation @Enumerated(EnumType.STRING)
        if (field.getType().isEnum()) {
            return "VARCHAR(255)";
        }
        Class<?> aClass = field.getType();
        String type = dbType.get(aClass);
        if (Objects.isNull(type)) {
            throw new UnknownFormatConversionException(aClass.getName());
        }
        return type;
    }

    private String getOneTypeCreateTable(Field field) {
        String fieldName = StrategyNaming.getFieldName(field);
        String typeField = field.isAnnotationPresent(Id.class)
                ? PRIMARY_KEY
                : getDatabaseType(field);
        String constraint = "";
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            constraint = !column.nullable() ? NOT_NULL : SPACE +
                                                         (column.unique() ? UNIQUE : SPACE);
        }
        String lineFormat = "\t%-20s %-24s %s";
        return lineFormat.formatted(fieldName, typeField, constraint);
    }

}
