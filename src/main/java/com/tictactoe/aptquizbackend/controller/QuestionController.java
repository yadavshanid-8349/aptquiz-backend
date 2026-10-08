package com.tictactoe.aptquizbackend.controller;

import com.tictactoe.aptquizbackend.entity.Question;
import com.tictactoe.aptquizbackend.entity.QuestionSet;
import com.tictactoe.aptquizbackend.repository.QuestionRepository;
import com.tictactoe.aptquizbackend.repository.QuestionSetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuestionSetRepository questionSetRepository;

    public QuestionController(
            QuestionRepository questionRepository,
            QuestionSetRepository questionSetRepository) {

        this.questionRepository = questionRepository;
        this.questionSetRepository = questionSetRepository;
    }

    // CREATE
    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return questionRepository.save(question);
    }

    // GET ALL
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    // UPDATE
    @PutMapping("/{id}")
    public Question updateQuestion(
            @PathVariable Long id,
            @RequestBody Question question) {

        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        existingQuestion.setQuestionText(question.getQuestionText());
        existingQuestion.setOptionA(question.getOptionA());
        existingQuestion.setOptionB(question.getOptionB());
        existingQuestion.setOptionC(question.getOptionC());
        existingQuestion.setOptionD(question.getOptionD());
        existingQuestion.setCorrectAnswer(question.getCorrectAnswer());
        existingQuestion.setTopic(question.getTopic());
        existingQuestion.setDifficulty(question.getDifficulty());
        existingQuestion.setQuestionSet(question.getQuestionSet());

        return questionRepository.save(existingQuestion);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteQuestion(@PathVariable Long id) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        questionRepository.delete(question);

        return "Question deleted successfully";
    }

    // ADD QUESTION TO QUESTION SET
    @PutMapping("/{questionId}/question-set/{questionSetId}")
    public Question addQuestionToSet(
            @PathVariable Long questionId,
            @PathVariable Long questionSetId) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        QuestionSet questionSet = questionSetRepository.findById(questionSetId)
                .orElseThrow(() -> new RuntimeException("Question Set not found"));

        question.setQuestionSet(questionSet);

        return questionRepository.save(question);
    }
}