package com.tictactoe.aptquizbackend.service;

import com.tictactoe.aptquizbackend.entity.GameState;
import com.tictactoe.aptquizbackend.entity.Question;
import com.tictactoe.aptquizbackend.entity.Room;
import com.tictactoe.aptquizbackend.repository.GameStateRepository;
import com.tictactoe.aptquizbackend.repository.QuestionRepository;
import com.tictactoe.aptquizbackend.repository.RoomRepository;

import org.springframework.stereotype.Service;

@Service
public class GameStateService {

    private final GameStateRepository gameStateRepository;
    private final RoomRepository roomRepository;
    private final QuestionRepository questionRepository;

    public GameStateService(
            GameStateRepository gameStateRepository,
            RoomRepository roomRepository,
            QuestionRepository questionRepository) {

        this.gameStateRepository = gameStateRepository;
        this.roomRepository = roomRepository;
        this.questionRepository = questionRepository;
    }

    // =========================
    // CREATE GAME STATE
    // =========================

    public GameState createGameState(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        GameState gameState = new GameState();

        gameState.setRoom(room);
        gameState.setCurrentQuestionIndex(0);
        gameState.setCurrentQuestionId(null);
        gameState.setQuestionStartTime(null);
        gameState.setQuestionEndTime(null);
        gameState.setActive(false);

        return gameStateRepository.save(gameState);
    }

    // =========================
    // GET GAME STATE
    // =========================

    public GameState getGameState(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        return gameStateRepository.findByRoom(room)
                .orElseThrow(() ->
                        new RuntimeException("Game state not found"));
    }

    // =========================
    // START QUESTION
    // =========================

    public GameState startQuestion(
            Long roomId,
            Long questionId,
            long durationSeconds) {

        GameState gameState;

        try {

            gameState = getGameState(roomId);

        } catch (RuntimeException e) {

            gameState = createGameState(roomId);
        }

        // Check question exists
        Question question =
                questionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found"));

        long startTime =
                System.currentTimeMillis();

        long endTime =
                startTime +
                        (durationSeconds * 1000);

        // Save current question
        gameState.setCurrentQuestionId(
                question.getId()
        );

        // Save timer
        gameState.setQuestionStartTime(
                startTime
        );

        gameState.setQuestionEndTime(
                endTime
        );

        // Question active
        gameState.setActive(true);

        return gameStateRepository.save(gameState);
    }

    // =========================
    // END QUESTION
    // =========================

    public GameState endQuestion(Long roomId) {

        GameState gameState =
                getGameState(roomId);

        gameState.setActive(false);

        return gameStateRepository.save(gameState);
    }

    // =========================
    // NEXT QUESTION
    // =========================

    public GameState nextQuestion(Long roomId) {

        GameState gameState =
                getGameState(roomId);

        gameState.setCurrentQuestionIndex(
                gameState.getCurrentQuestionIndex() + 1
        );

        gameState.setCurrentQuestionId(null);

        gameState.setQuestionStartTime(null);

        gameState.setQuestionEndTime(null);

        gameState.setActive(false);

        return gameStateRepository.save(gameState);
    }
}