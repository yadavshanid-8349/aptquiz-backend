package com.tictactoe.aptquizbackend.repository;

import com.tictactoe.aptquizbackend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}