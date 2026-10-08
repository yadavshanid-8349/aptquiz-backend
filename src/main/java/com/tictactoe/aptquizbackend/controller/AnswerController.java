package com.tictactoe.aptquizbackend.controller;

import com.tictactoe.aptquizbackend.entity.Answer;
import com.tictactoe.aptquizbackend.entity.RoomPlayer;
import com.tictactoe.aptquizbackend.repository.AnswerRepository;
import com.tictactoe.aptquizbackend.repository.RoomPlayerRepository;
import com.tictactoe.aptquizbackend.service.AnswerService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final RoomPlayerRepository roomPlayerRepository;
    private final AnswerRepository answerRepository;

    public AnswerController(
            AnswerService answerService,
            RoomPlayerRepository roomPlayerRepository,
            AnswerRepository answerRepository) {

        this.answerService = answerService;
        this.roomPlayerRepository = roomPlayerRepository;
        this.answerRepository = answerRepository;
    }

    // SUBMIT ANSWER
    @PostMapping
    public Answer submitAnswer(
            @RequestParam Long playerId,
            @RequestParam Long questionId,
            @RequestParam String selectedAnswer) {

        return answerService.submitAnswer(
                playerId,
                questionId,
                selectedAnswer
        );
    }

    // GET ALL ANSWERS OF PLAYER
    @GetMapping("/player/{playerId}")
    public List<Answer> getPlayerAnswers(
            @PathVariable Long playerId) {

        RoomPlayer player = roomPlayerRepository.findById(playerId)
                .orElseThrow(() ->
                        new RuntimeException("Player not found"));

        return answerRepository.findByPlayer(player);
    }
}