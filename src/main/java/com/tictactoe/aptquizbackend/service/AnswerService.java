package com.tictactoe.aptquizbackend.service;

import com.tictactoe.aptquizbackend.entity.Answer;
import com.tictactoe.aptquizbackend.entity.GameState;
import com.tictactoe.aptquizbackend.entity.Question;
import com.tictactoe.aptquizbackend.entity.RoomPlayer;
import com.tictactoe.aptquizbackend.repository.AnswerRepository;
import com.tictactoe.aptquizbackend.repository.GameStateRepository;
import com.tictactoe.aptquizbackend.repository.QuestionRepository;
import com.tictactoe.aptquizbackend.repository.RoomPlayerRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final QuestionRepository questionRepository;
    private final GameStateRepository gameStateRepository;

    public AnswerService(
            AnswerRepository answerRepository,
            RoomPlayerRepository roomPlayerRepository,
            QuestionRepository questionRepository,
            GameStateRepository gameStateRepository) {

        this.answerRepository = answerRepository;
        this.roomPlayerRepository = roomPlayerRepository;
        this.questionRepository = questionRepository;
        this.gameStateRepository = gameStateRepository;
    }

    public Answer submitAnswer(
            Long playerId,
            Long questionId,
            String selectedAnswer) {

        RoomPlayer player = roomPlayerRepository.findById(playerId)
                .orElseThrow(() ->
                        new RuntimeException("Player not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new RuntimeException("Question not found"));

        GameState gameState = gameStateRepository.findByRoom(
                player.getRoom()
        ).orElseThrow(() ->
                new RuntimeException("Game state not found"));

        long currentTime = System.currentTimeMillis();

        // Question active hai ya nahi
        if (!gameState.getActive()) {
            throw new RuntimeException("Question is not active");
        }

        // Time limit check
        if (currentTime > gameState.getQuestionEndTime()) {
            throw new RuntimeException("Answer submitted too late");
        }

        // Duplicate answer check
        List<Answer> previousAnswers =
                answerRepository.findByPlayer(player);

        boolean alreadyAnswered = previousAnswers.stream()
                .anyMatch(answer ->
                        answer.getQuestionId().equals(questionId));

        if (alreadyAnswered) {
            throw new RuntimeException(
                    "Player already answered this question"
            );
        }

        // Server khud correct answer check karega
        boolean correct = question.getCorrectAnswer()
                .equalsIgnoreCase(selectedAnswer);

        // Score calculate
        int score = 0;

        if (correct) {

            long remainingTime =
                    gameState.getQuestionEndTime() - currentTime;

            long totalTime =
                    gameState.getQuestionEndTime()
                            - gameState.getQuestionStartTime();

            int speedBonus = 0;

            if (totalTime > 0 && remainingTime > 0) {
                speedBonus = (int)
                        ((remainingTime * 50) / totalTime);
            }

            score = 100 + speedBonus;
        }

        Answer answer = new Answer();

        answer.setPlayer(player);
        answer.setQuestionId(questionId);
        answer.setSelectedAnswer(selectedAnswer);
        answer.setCorrect(correct);
        answer.setScore(score);
        answer.setAnsweredAt(currentTime);

        // Player total score update
        player.setScore(player.getScore() + score);

        roomPlayerRepository.save(player);

        return answerRepository.save(answer);
    }
}