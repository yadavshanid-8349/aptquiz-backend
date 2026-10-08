package com.tictactoe.aptquizbackend.repository;

import com.tictactoe.aptquizbackend.entity.GameState;
import com.tictactoe.aptquizbackend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameStateRepository extends JpaRepository<GameState, Long> {

    Optional<GameState> findByRoom(Room room);
}