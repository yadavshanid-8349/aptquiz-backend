package com.tictactoe.aptquizbackend.repository;

import com.tictactoe.aptquizbackend.entity.Answer;
import com.tictactoe.aptquizbackend.entity.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByPlayer(RoomPlayer player);
}