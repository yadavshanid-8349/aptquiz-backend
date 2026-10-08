package com.tictactoe.aptquizbackend.service;

import com.tictactoe.aptquizbackend.entity.GameState;
import com.tictactoe.aptquizbackend.entity.Question;
import com.tictactoe.aptquizbackend.entity.Room;
import com.tictactoe.aptquizbackend.repository.GameStateRepository;
import com.tictactoe.aptquizbackend.repository.RoomRepository;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameScheduler {

    private final GameStateRepository gameStateRepository;
    private final GameStateService gameStateService;
    private final GameService gameService;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GameScheduler(
            GameStateRepository gameStateRepository,
            GameStateService gameStateService,
            GameService gameService,
            RoomRepository roomRepository,
            SimpMessagingTemplate messagingTemplate) {

        this.gameStateRepository = gameStateRepository;
        this.gameStateService = gameStateService;
        this.gameService = gameService;
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 1000)
    public void checkQuestionTimers() {

        List<GameState> gameStates =
                gameStateRepository.findAll();

        long currentTime =
                System.currentTimeMillis();

        for (GameState gameState : gameStates) {

            // Question active nahi hai
            if (!Boolean.TRUE.equals(
                    gameState.getActive())) {

                continue;
            }

            Long endTime =
                    gameState.getQuestionEndTime();

            // Timer abhi khatam nahi hua
            if (endTime == null ||
                    currentTime < endTime) {

                continue;
            }

            Room room =
                    gameState.getRoom();

            if (room == null) {
                continue;
            }

            Long roomId =
                    room.getId();

            /*
             * Current question end
             */
            gameStateService.endQuestion(roomId);

            /*
             * Sab clients ko bataye ki
             * current question khatam ho gaya
             */
            messagingTemplate.convertAndSend(
                    "/topic/game/" + roomId,
                    "QUESTION_ENDED:"
                            + roomId
                            + ":"
                            + gameState.getCurrentQuestionId()
            );

            /*
             * IMPORTANT:
             * Question index increase karo.
             *
             * Isse same question dobara nahi aayega.
             */
            try {

                gameStateService.nextQuestion(roomId);

                /*
                 * Next question start karo
                 */
                Question nextQuestion =
                        gameService.startNextQuestion(
                                roomId,
                                15
                        );

                GameState newState =
                        gameStateService.getGameState(
                                roomId
                        );

                /*
                 * Next question sab players ko bhejo
                 */
                String response =
                        "QUESTION_STARTED:"
                                + room.getRoomCode()
                                + ":"
                                + nextQuestion.getId()
                                + ":"
                                + nextQuestion.getQuestionText()
                                + ":"
                                + nextQuestion.getOptionA()
                                + ":"
                                + nextQuestion.getOptionB()
                                + ":"
                                + nextQuestion.getOptionC()
                                + ":"
                                + nextQuestion.getOptionD()
                                + ":"
                                + newState.getQuestionStartTime()
                                + ":"
                                + newState.getQuestionEndTime();

                messagingTemplate.convertAndSend(
                        "/topic/game/" + roomId,
                        response
                );

            } catch (RuntimeException e) {

                /*
                 * Saare questions complete
                 */
                room.setStatus("FINISHED");

                roomRepository.save(room);

                messagingTemplate.convertAndSend(
                        "/topic/game/" + roomId,
                        "GAME_FINISHED:"
                                + roomId
                );
            }
        }
    }
}