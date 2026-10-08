package com.tictactoe.aptquizbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;

    private Integer score = 0;

    private Integer currentQuestion = 0;

    private Boolean connected = true;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}