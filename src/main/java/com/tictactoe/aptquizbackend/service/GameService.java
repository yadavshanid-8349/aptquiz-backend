
        package com.tictactoe.aptquizbackend.service;

import com.tictactoe.aptquizbackend.entity.GameState;
import com.tictactoe.aptquizbackend.entity.Question;
import com.tictactoe.aptquizbackend.entity.Room;
import com.tictactoe.aptquizbackend.repository.QuestionRepository;
import com.tictactoe.aptquizbackend.repository.RoomRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final RoomRepository roomRepository;
    private final QuestionRepository questionRepository;
    private final GameStateService gameStateService;

    public GameService(
            RoomRepository roomRepository,
            QuestionRepository questionRepository,
            GameStateService gameStateService) {

        this.roomRepository = roomRepository;
        this.questionRepository = questionRepository;
        this.gameStateService = gameStateService;
    }

    public Question startNextQuestion(
            Long roomId,
            long durationSeconds) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        List<Question> questions =
                questionRepository.findAll()
                        .stream()
                        .filter(q ->
                                q.getQuestionSet() != null
                                        && room.getQuestionSet() != null
                                        && q.getQuestionSet().getId()
                                        .equals(room.getQuestionSet().getId()))
                        .toList();

        if (questions.isEmpty()) {
            throw new RuntimeException(
                    "No questions found in this question set");
        }

        GameState gameState;

        try {
            gameState =
                    gameStateService.getGameState(roomId);
        } catch (RuntimeException e) {
            gameState =
                    gameStateService.createGameState(roomId);
        }

        int index =
                gameState.getCurrentQuestionIndex();

        if (index >= questions.size()) {
            throw new RuntimeException(
                    "All questions completed");
        }

        Question question =
                questions.get(index);

        gameStateService.startQuestion(
                roomId,
                question.getId(),
                durationSeconds
        );

        return question;
    }

    public boolean hasNextQuestion(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        List<Question> questions =
                questionRepository.findAll()
                        .stream()
                        .filter(q ->
                                q.getQuestionSet() != null
                                        && room.getQuestionSet() != null
                                        && q.getQuestionSet().getId()
                                        .equals(room.getQuestionSet().getId()))
                        .toList();

        GameState gameState;

        try {
            gameState =
                    gameStateService.getGameState(roomId);
        } catch (RuntimeException e) {
            return !questions.isEmpty();
        }

        return gameState.getCurrentQuestionIndex()
                < questions.size();
    }
}

