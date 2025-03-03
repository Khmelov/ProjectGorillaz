package com.javarush.lesson08;

public class DaoException extends RuntimeException {

    public DaoException(Exception cause) {
        super(cause);
    }

}
