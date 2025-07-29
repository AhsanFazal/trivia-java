package com.ahsanfazal.trivia.controller;

import com.ahsanfazal.trivia.dto.*;
import com.ahsanfazal.trivia.service.TriviaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class TriviaController {
  private static final Logger logger = LoggerFactory.getLogger(TriviaController.class);

  private final TriviaService triviaService;

  public TriviaController(TriviaService triviaService) {
    this.triviaService = triviaService;
  }

  @GetMapping("/questions")
  public ResponseEntity<List<QuestionDTO>> getQuestions(
      @RequestParam(defaultValue = "10") @Min(1) @Max(50) int amount,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String difficulty) {

    logger.info("Fetching {} questions, category: {}, difficulty: {}", amount, category, difficulty);

    List<QuestionDTO> questions = triviaService.getQuestions(amount, category, difficulty);

    return ResponseEntity.ok(questions);
  }

  @PostMapping("/checkanswers")
  public ResponseEntity<CheckAnswersResponseDTO> checkAnswers(@Valid @RequestBody CheckAnswersRequestDTO request) {
    logger.info("Checking {} answers", request.getAnswers().size());

    if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    CheckAnswersResponseDTO response = triviaService.checkAnswers(request);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("OK");
  }
}
