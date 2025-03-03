package com.javarush.lesson15.inheritance.model;

import jakarta.persistence.Entity;
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
public class Customer extends BasePerson {

    private String firstName;
    private String lastName;

}
