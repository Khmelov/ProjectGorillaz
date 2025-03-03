package com.javarush.khmelov.entity;

import java.io.Serializable;

/**
 * Parent any entity. Use as parent in wildcard
 */
public interface AbstractEntity extends Serializable {

    Long getId();

    void setId(Long id);

}
