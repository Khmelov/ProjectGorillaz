package com.javarush.khmelov.entity;

import com.javarush.khmelov.dto.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@ToString(exclude = {"quests", "games", "questsInGame"})
public class User implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user")
    UserInfo userInfo;

    @OneToMany(mappedBy = "author")
    private final Collection<Quest> quests = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "game",
            joinColumns = @JoinColumn(
                    name = "quest_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "users_id",
                    referencedColumnName = "id")
    )
    Collection<Quest> questsInGame = new ArrayList<>();


    public void addQuest(Quest quest) {
        quest.setAuthor(this);
        quests.add(quest);
    }

    @OneToMany
    @JoinColumn(name = "users_id")
    private final Collection<Game> games = new ArrayList<>();

    public void addGames(Game game) {
        game.setUserId(this.getId());
        games.add(game);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return 42;
    }
}
