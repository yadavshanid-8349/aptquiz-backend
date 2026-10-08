package com.tictactoe.aptquizbackend.controller;

import com.tictactoe.aptquizbackend.entity.GameState;
import com.tictactoe.aptquizbackend.entity.Question;
import com.tictactoe.aptquizbackend.entity.Room;
import com.tictactoe.aptquizbackend.repository.RoomRepository;
import com.tictactoe.aptquizbackend.service.GameService;
import com.tictactoe.aptquizbackend.service.GameStateService;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final GameStateService gameStateService;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(
            GameService gameService,
            GameStateService gameStateService,
            RoomRepository roomRepository,
            SimpMessagingTemplate messagingTemplate) {

        this.gameService = gameService;
        this.gameStateService = gameStateService;
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/{roomId}/next")
    public Question nextQuestion(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "15")
            long durationSeconds) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        if (!"STARTED".equals(room.getStatus())) {
            throw new RuntimeException(
                    "Game has not started");
        }

        Question question =
                gameService.startNextQuestion(
                        roomId,
                        durationSeconds
                );

        GameState gameState =
                gameStateService.getGameState(roomId);

        String response =
                "QUESTION_STARTED:"
                        + room.getRoomCode()
                        + ":"
                        + question.getId()
                        + ":"
                        + question.getQuestionText()
                        + ":"
                        + question.getOptionA()
                        + ":"
                        + question.getOptionB()
                        + ":"
                        + question.getOptionC()
                        + ":"
                        + question.getOptionD()
                        + ":"
                        + gameState.getQuestionStartTime()
                        + ":"
                        + gameState.getQuestionEndTime();

        messagingTemplate.convertAndSend(
                "/topic/game/" + roomId,
                response
        );

        return question;
    }

    @GetMapping("/{roomId}/has-next")
    public boolean hasNextQuestion(
            @PathVariable Long roomId) {

        return gameService.hasNextQuestion(roomId);
    }
}