package com.javarush.lesson12.shmibernate;


import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Developer implements AbstractEntity {
    @Id
    Long id;

    String name;
    String email;
    String phone;

    Role role;
}
