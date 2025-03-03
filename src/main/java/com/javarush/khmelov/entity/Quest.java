package com.javarush.khmelov.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;

import java.util.ArrayList;
import java.util.Collection;


@FetchProfile(name = Quest.LAZY_QUESTIONS_AND_JOIN_AUTHOR_FETCH,
        fetchOverrides = {
                @FetchProfile.FetchOverride(
                        entity = Quest.class,
                        association = "questions",
                        mode = FetchMode.SUBSELECT
                ),
                @FetchProfile.FetchOverride(
                        entity = Quest.class,
                        association = "author",
                        mode = FetchMode.JOIN
                ), @FetchProfile.FetchOverride(
                entity = User.class,
                association = "userInfo",
                mode = FetchMode.JOIN
        ),
        })

@NamedEntityGraph(
        name = Quest.GRAPH_QUEST_QUESTIONS_AUTHOR_FETCH,
        attributeNodes = {
                @NamedAttributeNode("questions"),
                @NamedAttributeNode(value = "author", subgraph = "authorUserInfo"),
        },
        subgraphs = {
                @NamedSubgraph(name = "authorUserInfo",
                        attributeNodes = @NamedAttributeNode("userInfo")
                ),
        }

)

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"questions", "author", "users", "text"})
public class Quest implements AbstractEntity {
    public static final String LAZY_QUESTIONS_AND_JOIN_AUTHOR_FETCH = "lazy_questions_and_join_author_fetch_profile";
    public static final String GRAPH_QUEST_QUESTIONS_AUTHOR_FETCH = "GRAPH_QUEST_QUESTIONS_AUTHOR_FETCH";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User author;

    @Column(name = "start_question_id")
    private Long startQuestionId;

    //@Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "questId", fetch = FetchType.LAZY)
    private final Collection<Question> questions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "game",
            inverseJoinColumns = @JoinColumn(
                    name = "quest_id",
                    referencedColumnName = "id"),
            joinColumns = @JoinColumn(name = "users_id",
                    referencedColumnName = "id")
    )
    private Collection<User> users;

}
