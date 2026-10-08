package com.tictactoe.aptquizbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String selectedAnswer;

    private Boolean correct;

    private Integer score;

    private Long questionId;

    private Long answeredAt;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private RoomPlayer player;
}