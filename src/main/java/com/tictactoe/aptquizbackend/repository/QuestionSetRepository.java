package com.tictactoe.aptquizbackend.repository;

import com.tictactoe.aptquizbackend.entity.QuestionSet;
import com.tictactoe.aptquizbackend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionSetRepository
        extends JpaRepository<QuestionSet, Long> {

    List<QuestionSet> findByOwner(User owner);
}