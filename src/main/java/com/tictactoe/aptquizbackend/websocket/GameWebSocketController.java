
        package com.tictactoe.aptquizbackend.websocket;

import com.tictactoe.aptquizbackend.entity.Answer;
import com.tictactoe.aptquizbackend.entity.GameState;
import com.tictactoe.aptquizbackend.entity.Question;
import com.tictactoe.aptquizbackend.entity.Room;
import com.tictactoe.aptquizbackend.entity.RoomPlayer;

import com.tictactoe.aptquizbackend.repository.QuestionRepository;
import com.tictactoe.aptquizbackend.repository.RoomPlayerRepository;
import com.tictactoe.aptquizbackend.repository.RoomRepository;

import com.tictactoe.aptquizbackend.service.AnswerService;
import com.tictactoe.aptquizbackend.service.GameService;
import com.tictactoe.aptquizbackend.service.GameStateService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameWebSocketController {

    private final RoomRepository roomRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final QuestionRepository questionRepository;
    private final GameStateService gameStateService;
    private final AnswerService answerService;
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameWebSocketController(
            RoomRepository roomRepository,
            RoomPlayerRepository roomPlayerRepository,
            QuestionRepository questionRepository,
            GameStateService gameStateService,
            AnswerService answerService,
            GameService gameService,
            SimpMessagingTemplate messagingTemplate) {

        this.roomRepository = roomRepository;
        this.roomPlayerRepository = roomPlayerRepository;
        this.questionRepository = questionRepository;
        this.gameStateService = gameStateService;
        this.answerService = answerService;
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    // =========================
    // PLAYER JOIN
    // =========================

    @MessageMapping("/join")
    @SendTo("/topic/players")
    public String playerJoined(String data) {

        String[] parts = data.split(":", 2);

        if (parts.length != 2) {
            return "Invalid join data";
        }

        String roomCode = parts[0];
        String playerName = parts[1];

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        if (!room.getStatus().equals("WAITING")) {
            return "Room has already started";
        }

        List<RoomPlayer> players =
                roomPlayerRepository.findByRoom(room);

        if (players.size() >= room.getMaxPlayers()) {
            return "Room is full";
        }

        boolean nameExists =
                roomPlayerRepository
                        .findByRoomAndPlayerName(room, playerName)
                        .isPresent();

        if (nameExists) {
            return "Player name already exists";
        }

        RoomPlayer player = new RoomPlayer();

        player.setPlayerName(playerName);
        player.setScore(0);
        player.setCurrentQuestion(0);
        player.setConnected(true);
        player.setRoom(room);

        roomPlayerRepository.save(player);

        return "PLAYER_JOINED:"
                + playerName
                + ":"
                + roomCode;
    }

    // =========================
    // GAME START
    // =========================

    @MessageMapping("/start")
    public String gameStarted(String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        if (!room.getStatus().equals("WAITING")) {
            return "Game already started";
        }

        room.setStatus("STARTED");
        roomRepository.save(room);

        messagingTemplate.convertAndSend(
                "/topic/game/" + room.getId(),
                "GAME_STARTED:" + roomCode
        );

        return "GAME_STARTED:" + roomCode;
    }

    // =========================
    // START QUESTION
    // =========================

    @MessageMapping("/question")
    public String questionStarted(String data) {

        String[] parts = data.split(":");

        if (parts.length != 3) {
            return "Invalid question data";
        }

        String roomCode = parts[0];

        Long questionId;

        long durationSeconds;

        try {

            questionId =
                    Long.parseLong(parts[1]);

            durationSeconds =
                    Long.parseLong(parts[2]);

        } catch (NumberFormatException e) {

            return "Invalid question ID or duration";
        }

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"));

        if (!room.getStatus().equals("STARTED")) {
            return "Game has not started";
        }

        Question question =
                questionRepository
                        .findById(questionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found"));

        GameState gameState =
                gameStateService.startQuestion(
                        room.getId(),
                        questionId,
                        durationSeconds
                );

        String response =
                "QUESTION_STARTED:"
                        + roomCode
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
                "/topic/game/" + room.getId(),
                response
        );

        return response;
    }

    // =========================
    // NEXT QUESTION
    // =========================

    @MessageMapping("/next-question")
    public String nextQuestion(String roomIdData) {

        Long roomId;

        try {

            roomId =
                    Long.parseLong(roomIdData);

        } catch (NumberFormatException e) {

            return "Invalid room ID";
        }

        try {

            gameStateService.nextQuestion(roomId);

            Question question =
                    gameService.startNextQuestion(
                            roomId,
                            15
                    );

            Room room =
                    roomRepository.findById(roomId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Room not found"));

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

            return response;

        } catch (RuntimeException e) {

            messagingTemplate.convertAndSend(
                    "/topic/game/" + roomId,
                    "GAME_FINISHED:" + roomId
            );

            return "GAME_FINISHED:" + roomId;
        }
    }

    // =========================
    // ANSWER
    // =========================

    @MessageMapping("/answer")
    public String playerAnswered(String data) {

        String[] parts =
                data.split(":", 3);

        if (parts.length != 3) {
            return "Invalid answer data";
        }

        Long playerId;
        Long questionId;

        String selectedAnswer =
                parts[2];

        try {

            playerId =
                    Long.parseLong(parts[0]);

            questionId =
                    Long.parseLong(parts[1]);

        } catch (NumberFormatException e) {

            return "Invalid player ID or question ID";
        }

        try {

            Answer answer =
                    answerService.submitAnswer(
                            playerId,
                            questionId,
                            selectedAnswer
                    );

            RoomPlayer player =
                    answer.getPlayer();

            Room room =
                    player.getRoom();

            List<RoomPlayer> players =
                    roomPlayerRepository
                            .findByRoomOrderByScoreDesc(room);

            StringBuilder leaderboard =
                    new StringBuilder(
                            "LEADERBOARD:");

            for (int i = 0;
                 i < players.size();
                 i++) {

                RoomPlayer currentPlayer =
                        players.get(i);

                leaderboard
                        .append(i + 1)
                        .append("|")
                        .append(currentPlayer.getId())
                        .append("|")
                        .append(currentPlayer.getPlayerName())
                        .append("|")
                        .append(currentPlayer.getScore());

                if (i < players.size() - 1) {
                    leaderboard.append(",");
                }
            }

            messagingTemplate.convertAndSend(
                    "/topic/game/" + room.getId(),
                    leaderboard.toString()
            );

            return leaderboard.toString();

        } catch (RuntimeException e) {

            return "ANSWER_REJECTED:"
                    + e.getMessage();
        }
    }
}
