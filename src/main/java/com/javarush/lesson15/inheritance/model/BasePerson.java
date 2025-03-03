package com.javarush.lesson15.inheritance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BasePerson extends AbstractParent {

    private String name;
    private String email;

}
