package com.tictactoe.aptquizbackend.controller;

import com.tictactoe.aptquizbackend.entity.QuestionSet;
import com.tictactoe.aptquizbackend.entity.Room;
import com.tictactoe.aptquizbackend.entity.RoomPlayer;
import com.tictactoe.aptquizbackend.entity.User;

import com.tictactoe.aptquizbackend.repository.QuestionSetRepository;
import com.tictactoe.aptquizbackend.repository.RoomPlayerRepository;
import com.tictactoe.aptquizbackend.repository.RoomRepository;
import com.tictactoe.aptquizbackend.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final QuestionSetRepository questionSetRepository;
    private final RoomPlayerRepository roomPlayerRepository;

    public RoomController(
            RoomRepository roomRepository,
            UserRepository userRepository,
            QuestionSetRepository questionSetRepository,
            RoomPlayerRepository roomPlayerRepository) {

        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.questionSetRepository = questionSetRepository;
        this.roomPlayerRepository = roomPlayerRepository;
    }


    // =========================================================
    // CREATE ROOM
    // =========================================================

    @PostMapping
    public Room createRoom(
            @RequestParam Long hostId,
            @RequestParam Long questionSetId) {

        // Find host
        User host = userRepository.findById(hostId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Host not found"));


        // Find question set
        QuestionSet questionSet =
                questionSetRepository.findById(questionSetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question Set not found"));


        // =====================================================
        // IMPORTANT:
        // User sirf apne question set se room bana sakta hai
        // =====================================================

        if (questionSet.getOwner() == null) {

            throw new RuntimeException(
                    "Question Set has no owner");
        }

        if (!questionSet.getOwner()
                .getId()
                .equals(hostId)) {

            throw new RuntimeException(
                    "You can only use your own question set");
        }


        // =====================================================
        // CREATE ROOM
        // =====================================================

        Room room = new Room();

        room.setRoomCode(generateRoomCode());
        room.setRoomName("AptiQuiz Room");
        room.setStatus("WAITING");
        room.setMaxPlayers(50);

        // Host
        room.setHost(host);

        // Question Set
        room.setQuestionSet(questionSet);


        Room savedRoom =
                roomRepository.save(room);


        // =====================================================
        // ADD HOST AS PLAYER
        // =====================================================

        RoomPlayer hostPlayer =
                new RoomPlayer();

        hostPlayer.setPlayerName(
                host.getName());

        hostPlayer.setScore(0);

        hostPlayer.setCurrentQuestion(0);

        hostPlayer.setConnected(true);

        hostPlayer.setRoom(savedRoom);


        roomPlayerRepository.save(
                hostPlayer);


        return savedRoom;
    }


    // =========================================================
    // GET ALL ROOMS
    // =========================================================

    @GetMapping
    public List<Room> getAllRooms() {

        return roomRepository.findAll();
    }


    // =========================================================
    // GET ROOM BY ID
    // =========================================================

    @GetMapping("/{id}")
    public Room getRoomById(
            @PathVariable Long id) {

        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Room not found"));
    }


    // =========================================================
    // GET ROOM BY CODE
    // =========================================================

    @GetMapping("/code/{roomCode}")
    public Room getRoomByCode(
            @PathVariable String roomCode) {

        return roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Room not found"));
    }


    // =========================================================
    // START ROOM
    // =========================================================

    @PutMapping("/{id}/start")
    public Room startRoom(
            @PathVariable Long id) {

        Room room =
                roomRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"));


        if ("STARTED".equals(room.getStatus())) {
            return room;
        }

        if ("FINISHED".equals(room.getStatus())) {
            throw new RuntimeException(
                    "Room has already finished");
        }

        room.setStatus("STARTED");


        return roomRepository.save(room);
    }


    // =========================================================
    // DELETE ROOM
    // =========================================================

    @DeleteMapping("/{id}")
    public String deleteRoom(
            @PathVariable Long id) {

        Room room =
                roomRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"));


        roomRepository.delete(room);


        return "Room deleted successfully";
    }


    // =========================================================
    // GENERATE ROOM CODE
    // =========================================================

    private String generateRoomCode() {

        String code;

        do {

            code = UUID.randomUUID()
                    .toString()
                    .substring(0, 6)
                    .toUpperCase();

        } while (
                roomRepository
                        .findByRoomCode(code)
                        .isPresent()
        );

        return code;
    }
}