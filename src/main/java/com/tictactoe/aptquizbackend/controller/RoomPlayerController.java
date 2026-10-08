package com.tictactoe.aptquizbackend.controller;

import com.tictactoe.aptquizbackend.dto.LeaderboardPlayer;
import com.tictactoe.aptquizbackend.entity.Room;
import com.tictactoe.aptquizbackend.entity.RoomPlayer;
import com.tictactoe.aptquizbackend.repository.RoomPlayerRepository;
import com.tictactoe.aptquizbackend.repository.RoomRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomPlayerController {

    private final RoomRepository roomRepository;
    private final RoomPlayerRepository roomPlayerRepository;

    public RoomPlayerController(
            RoomRepository roomRepository,
            RoomPlayerRepository roomPlayerRepository) {

        this.roomRepository = roomRepository;
        this.roomPlayerRepository = roomPlayerRepository;
    }


    // =========================
    // JOIN ROOM
    // =========================

    @PostMapping("/{roomCode}/join")
    public RoomPlayer joinRoom(
            @PathVariable String roomCode,
            @RequestParam String playerName) {

        Room room = roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        if (!room.getStatus().equals("WAITING")) {

            throw new RuntimeException(
                    "Room has already started");
        }

        List<RoomPlayer> players =
                roomPlayerRepository.findByRoom(room);

        if (players.size() >= room.getMaxPlayers()) {

            throw new RuntimeException(
                    "Room is full");
        }

        boolean nameExists =
                roomPlayerRepository
                        .findByRoomAndPlayerName(
                                room,
                                playerName
                        )
                        .isPresent();

        if (nameExists) {

            throw new RuntimeException(
                    "Player name already exists in this room");
        }

        RoomPlayer player =
                new RoomPlayer();

        player.setPlayerName(playerName);
        player.setScore(0);
        player.setCurrentQuestion(0);
        player.setConnected(true);
        player.setRoom(room);

        return roomPlayerRepository.save(player);
    }


    // =========================
    // GET PLAYERS
    // =========================

    @GetMapping("/{roomCode}/players")
    public List<RoomPlayer> getPlayers(
            @PathVariable String roomCode) {

        Room room = roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        return roomPlayerRepository.findByRoom(room);
    }


    // =========================
    // RECONNECT PLAYER
    // =========================

    @PutMapping("/player/{playerId}/reconnect")
    public RoomPlayer reconnectPlayer(
            @PathVariable Long playerId) {

        RoomPlayer player =
                roomPlayerRepository
                        .findById(playerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Player not found"));

        player.setConnected(true);

        return roomPlayerRepository.save(player);
    }


    // =========================
    // DISCONNECT PLAYER
    // =========================

    @PutMapping("/player/{playerId}/disconnect")
    public RoomPlayer disconnectPlayer(
            @PathVariable Long playerId) {

        RoomPlayer player =
                roomPlayerRepository
                        .findById(playerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Player not found"));

        player.setConnected(false);

        return roomPlayerRepository.save(player);
    }


    // =========================
    // LEADERBOARD
    // =========================

    @GetMapping("/{roomCode}/leaderboard")
    public List<LeaderboardPlayer> getLeaderboard(
            @PathVariable String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        List<RoomPlayer> players =
                roomPlayerRepository
                        .findByRoomOrderByScoreDesc(room);

        return players.stream()
                .map(player ->
                        new LeaderboardPlayer(
                                player.getId(),
                                player.getPlayerName(),
                                player.getScore()
                        )
                )
                .toList();
    }
}