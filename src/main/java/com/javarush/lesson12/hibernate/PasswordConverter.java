package com.javarush.lesson12.hibernate;

import jakarta.persistence.AttributeConverter;

public class PasswordConverter implements AttributeConverter<Password, String> {
    @Override
    public String convertToDatabaseColumn(Password attribute) {
        return reverse(attribute.getPassword());
    }

    @Override
    public Password convertToEntityAttribute(String dbData) {
        return Password.builder().password(reverse(dbData)).build();
    }

    private static String reverse(String password) {
        return new StringBuilder(password).reverse().toString();
    }
}
