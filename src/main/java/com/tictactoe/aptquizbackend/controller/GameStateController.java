package com.tictactoe.aptquizbackend.controller;

import com.tictactoe.aptquizbackend.entity.GameState;
import com.tictactoe.aptquizbackend.service.GameStateService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game-state")
public class GameStateController {

    private final GameStateService gameStateService;

    public GameStateController(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    // =========================
    // CREATE GAME STATE
    // =========================

    @PostMapping("/{roomId}")
    public GameState createGameState(
            @PathVariable Long roomId) {

        return gameStateService.createGameState(roomId);
    }

    // =========================
    // GET GAME STATE
    // =========================

    @GetMapping("/{roomId}")
    public GameState getGameState(
            @PathVariable Long roomId) {

        return gameStateService.getGameState(roomId);
    }

    // =========================
    // START QUESTION
    // =========================

    @PostMapping("/{roomId}/start")
    public GameState startQuestion(
            @PathVariable Long roomId,
            @RequestParam Long questionId,
            @RequestParam(defaultValue = "15")
            long durationSeconds) {

        return gameStateService.startQuestion(
                roomId,
                questionId,
                durationSeconds
        );
    }

    // =========================
    // END QUESTION
    // =========================

    @PostMapping("/{roomId}/end")
    public GameState endQuestion(
            @PathVariable Long roomId) {

        return gameStateService.endQuestion(roomId);
    }

    // =========================
    // NEXT QUESTION
    // =========================

    @PostMapping("/{roomId}/next")
    public GameState nextQuestion(
            @PathVariable Long roomId) {

        return gameStateService.nextQuestion(roomId);
    }
}