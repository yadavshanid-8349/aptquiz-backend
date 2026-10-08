package com.tictactoe.aptquizbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_sets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String topic;

    private String difficulty;

    // Question set kis user ka hai
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}