package com.tictactoe.aptquizbackend.repository;

import com.tictactoe.aptquizbackend.entity.Room;
import com.tictactoe.aptquizbackend.entity.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomPlayerRepository
        extends JpaRepository<RoomPlayer, Long> {

    List<RoomPlayer> findByRoom(Room room);

    Optional<RoomPlayer> findByRoomAndPlayerName(
            Room room,
            String playerName
    );

    List<RoomPlayer> findByRoomOrderByScoreDesc(
            Room room
    );
}