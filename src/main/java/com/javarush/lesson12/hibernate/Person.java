package com.javarush.lesson12.hibernate;

import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@ToString
public class Person implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String login;

    ///@Convert(converter = PasswordConverter.class)
    @Type(PasswordType.class)
    Password password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
