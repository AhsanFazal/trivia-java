package com.ahsanfazal.trivia.service;

import com.ahsanfazal.trivia.client.OpenTriviaClient;
import com.ahsanfazal.trivia.dto.*;
import com.ahsanfazal.trivia.exception.QuestionNotFoundException;
import com.ahsanfazal.trivia.exception.SessionExpiredException;
import com.ahsanfazal.trivia.model.Question;
import com.ahsanfazal.trivia.model.TriviaApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TriviaService {
  private static final Logger logger = LoggerFactory.getLogger(TriviaService.class);
  private static final String QUESTIONS_SESSION_KEY = "trivia_questions";

  private final OpenTriviaClient triviaClient;

  public TriviaService(OpenTriviaClient triviaClient) {
    this.triviaClient = triviaClient;
  }

  public List<QuestionDTO> getQuestions(int amount, String category, String difficulty) {
    HttpSession session = getSession();

    TriviaApiResponse response = triviaClient.fetchQuestions(amount, category, difficulty);

    Map<String, Question> questions = response.getResults().stream()
        .map(this::mapToQuestion)
        .collect(Collectors.toMap(Question::getId, q -> q));

    session.setAttribute(QUESTIONS_SESSION_KEY, questions);
    logger.info("Stored {} questions in session {}", questions.size(), session.getId());

    return questions.values().stream()
        .map(this::mapToQuestionDTO)
        .collect(Collectors.toList());
  }

  public CheckAnswersResponseDTO checkAnswers(CheckAnswersRequestDTO request) {
    HttpSession session = getSession();
    Map<String, Question> storedQuestions = getStoredQuestions(session);

    if (storedQuestions.isEmpty()) {
      throw new SessionExpiredException("No questions found in session. Please request new questions.");
    }

    List<AnswerValidationDTO> results = new ArrayList<>();
    int correctCount = 0;

    for (AnswerDTO answer : request.getAnswers()) {
      Question question = storedQuestions.get(answer.getQuestionId());

      if (question == null) {
        throw new QuestionNotFoundException("Question not found: " + answer.getQuestionId());
      }

      boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answer.getAnswer());
      if (isCorrect) {
        correctCount++;
      }

      results.add(new AnswerValidationDTO(
          answer.getQuestionId(),
          isCorrect,
          question.getCorrectAnswer()));
    }

    session.removeAttribute(QUESTIONS_SESSION_KEY);
    logger.info("Validated {} answers for session {}", results.size(), session.getId());

    return new CheckAnswersResponseDTO(results, results.size(), correctCount);
  }

  private Question mapToQuestion(TriviaApiResponse.TriviaApiQuestion apiQuestion) {
    Question question = new Question();
    question.setQuestion(apiQuestion.getQuestion());
    question.setCorrectAnswer(apiQuestion.getCorrectAnswer());
    question.setIncorrectAnswers(apiQuestion.getIncorrectAnswers());
    question.setCategory(apiQuestion.getCategory());
    question.setType(apiQuestion.getType());
    question.setDifficulty(apiQuestion.getDifficulty());
    return question;
  }

  private QuestionDTO mapToQuestionDTO(Question question) {
    List<String> allOptions = new ArrayList<>();
    allOptions.add(question.getCorrectAnswer());
    allOptions.addAll(question.getIncorrectAnswers());
    Collections.shuffle(allOptions);

    return new QuestionDTO(
        question.getId(),
        question.getQuestion(),
        allOptions,
        question.getCategory(),
        question.getDifficulty());
  }

  private HttpSession getSession() {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    return attr.getRequest().getSession(true);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Question> getStoredQuestions(HttpSession session) {
    Object questions = session.getAttribute(QUESTIONS_SESSION_KEY);
    return questions instanceof Map ? (Map<String, Question>) questions : new HashMap<>();
  }
}
