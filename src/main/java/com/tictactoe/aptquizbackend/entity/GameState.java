package com.tictactoe.aptquizbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "room_id", unique = true)
    private Room room;

    // Current question ka index
    private Integer currentQuestionIndex = 0;

    // Current question ki ID
    private Long currentQuestionId;

    // Server-side timer
    private Long questionStartTime;

    private Long questionEndTime;

    // Question currently active hai ya nahi
    private Boolean active = false;
}