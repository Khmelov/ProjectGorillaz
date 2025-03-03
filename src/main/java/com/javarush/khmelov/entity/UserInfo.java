package com.javarush.khmelov.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class UserInfo implements AbstractEntity {

    @Id
    @Column(name = "user_id")
    Long id;

    String address;

    String phone;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
}
