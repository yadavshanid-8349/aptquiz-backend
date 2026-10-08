package com.tictactoe.aptquizbackend.controller;

import com.tictactoe.aptquizbackend.entity.Question;
import com.tictactoe.aptquizbackend.entity.QuestionSet;
import com.tictactoe.aptquizbackend.entity.User;
import com.tictactoe.aptquizbackend.repository.QuestionRepository;
import com.tictactoe.aptquizbackend.repository.QuestionSetRepository;
import com.tictactoe.aptquizbackend.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question-sets")
public class QuestionSetController {

    private final QuestionSetRepository questionSetRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionSetController(
            QuestionSetRepository questionSetRepository,
            QuestionRepository questionRepository,
            UserRepository userRepository) {

        this.questionSetRepository = questionSetRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    // Create question set
    @PostMapping
    public QuestionSet createQuestionSet(
            @RequestParam Long ownerId,
            @RequestBody QuestionSet questionSet) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        questionSet.setOwner(owner);

        return questionSetRepository.save(questionSet);
    }

    // Sirf current user ke question sets
    @GetMapping("/user/{userId}")
    public List<QuestionSet> getMyQuestionSets(
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return questionSetRepository.findByOwner(user);
    }

    @GetMapping
    public List<QuestionSet> getAllQuestionSets() {
        return questionSetRepository.findAll();
    }

    @GetMapping("/{id}")
    public QuestionSet getQuestionSetById(
            @PathVariable Long id) {

        return questionSetRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Question Set not found"));
    }

    // Update
    @PutMapping("/{id}")
    public QuestionSet updateQuestionSet(
            @PathVariable Long id,
            @RequestParam Long ownerId,
            @RequestBody QuestionSet questionSet) {

        QuestionSet existing =
                questionSetRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question Set not found"));

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException(
                    "You are not the owner of this question set");
        }

        existing.setTitle(questionSet.getTitle());
        existing.setDescription(questionSet.getDescription());
        existing.setTopic(questionSet.getTopic());
        existing.setDifficulty(questionSet.getDifficulty());

        return questionSetRepository.save(existing);
    }

    // Delete
    @DeleteMapping("/{id}")
    public String deleteQuestionSet(
            @PathVariable Long id,
            @RequestParam Long ownerId) {

        QuestionSet questionSet =
                questionSetRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question Set not found"));

        if (!questionSet.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException(
                    "You are not the owner of this question set");
        }

        questionSetRepository.delete(questionSet);

        return "Question Set deleted successfully";
    }

    // Questions of a question set
    @GetMapping("/{id}/questions")
    public List<Question> getQuestionsByQuestionSet(
            @PathVariable Long id) {

        QuestionSet questionSet =
                questionSetRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question Set not found"));

        return questionRepository.findAll()
                .stream()
                .filter(question ->
                        question.getQuestionSet() != null
                                && question.getQuestionSet()
                                .getId()
                                .equals(questionSet.getId()))
                .toList();
    }
}