package com.ahsanfazal.trivia.service;

import com.ahsanfazal.trivia.client.OpenTriviaClient;
import com.ahsanfazal.trivia.dto.*;
import com.ahsanfazal.trivia.exception.QuestionNotFoundException;
import com.ahsanfazal.trivia.exception.SessionExpiredException;
import com.ahsanfazal.trivia.model.TriviaApiResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriviaServiceTest {

  @Mock
  private OpenTriviaClient triviaClient;

  @InjectMocks
  private TriviaService triviaService;

  private MockHttpServletRequest request;
  private MockHttpSession session;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    session = new MockHttpSession();
    request.setSession(session);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  void getQuestions_Success() {
    TriviaApiResponse mockResponse = createMockApiResponse();
    when(triviaClient.fetchQuestions(10, null, null)).thenReturn(mockResponse);

    List<QuestionDTO> questions = triviaService.getQuestions(10, null, null);

    assertNotNull(questions);
    assertEquals(1, questions.size());
    QuestionDTO question = questions.get(0);
    assertEquals("What is 2+2?", question.getQuestion());
    assertEquals(4, question.getOptions().size());
    assertTrue(question.getOptions().contains("4"));
    assertTrue(question.getOptions().contains("3"));

    assertNotNull(session.getAttribute("trivia_questions"));
  }

  @Test
  void checkAnswers_CorrectAnswer() {
    setupSessionWithQuestion();

    AnswerDTO answer = new AnswerDTO("test-id", "4");
    CheckAnswersRequestDTO request = new CheckAnswersRequestDTO(Arrays.asList(answer));

    CheckAnswersResponseDTO response = triviaService.checkAnswers(request);

    assertNotNull(response);
    assertEquals(1, response.getTotalQuestions());
    assertEquals(1, response.getCorrectAnswers());
    assertEquals(100.0, response.getScore());
    assertTrue(response.getResults().get(0).isCorrect());
    assertEquals("4", response.getResults().get(0).getCorrectAnswer());
  }

  @Test
  void checkAnswers_IncorrectAnswer() {
    setupSessionWithQuestion();

    AnswerDTO answer = new AnswerDTO("test-id", "3");
    CheckAnswersRequestDTO request = new CheckAnswersRequestDTO(Arrays.asList(answer));

    CheckAnswersResponseDTO response = triviaService.checkAnswers(request);

    assertNotNull(response);
    assertEquals(1, response.getTotalQuestions());
    assertEquals(0, response.getCorrectAnswers());
    assertEquals(0.0, response.getScore());
    assertFalse(response.getResults().get(0).isCorrect());
    assertEquals("4", response.getResults().get(0).getCorrectAnswer());
  }

  @Test
  void checkAnswers_NoQuestionsInSession_ThrowsException() {
    AnswerDTO answer = new AnswerDTO("test-id", "4");
    CheckAnswersRequestDTO request = new CheckAnswersRequestDTO(Arrays.asList(answer));

    assertThrows(SessionExpiredException.class, () -> {
      triviaService.checkAnswers(request);
    });
  }

  @Test
  void checkAnswers_QuestionNotFound_ThrowsException() {
    setupSessionWithQuestion();

    AnswerDTO answer = new AnswerDTO("wrong-id", "4");
    CheckAnswersRequestDTO request = new CheckAnswersRequestDTO(Arrays.asList(answer));

    assertThrows(QuestionNotFoundException.class, () -> {
      triviaService.checkAnswers(request);
    });
  }

  @Test
  void checkAnswers_ClearsSessionAfterValidation() {
    setupSessionWithQuestion();

    AnswerDTO answer = new AnswerDTO("test-id", "4");
    CheckAnswersRequestDTO request = new CheckAnswersRequestDTO(Arrays.asList(answer));

    triviaService.checkAnswers(request);

    assertNull(session.getAttribute("trivia_questions"));
  }

  private TriviaApiResponse createMockApiResponse() {
    TriviaApiResponse response = new TriviaApiResponse();
    response.setResponseCode(0);

    TriviaApiResponse.TriviaApiQuestion question = new TriviaApiResponse.TriviaApiQuestion();
    question.setQuestion("What is 2+2?");
    question.setCorrectAnswer("4");
    question.setIncorrectAnswers(Arrays.asList("3", "5", "6"));
    question.setCategory("Math");
    question.setDifficulty("easy");
    question.setType("multiple");

    response.setResults(Arrays.asList(question));
    return response;
  }

  private void setupSessionWithQuestion() {
    com.ahsanfazal.trivia.model.Question question = new com.ahsanfazal.trivia.model.Question();
    question.setId("test-id");
    question.setQuestion("What is 2+2?");
    question.setCorrectAnswer("4");
    question.setIncorrectAnswers(Arrays.asList("3", "5", "6"));

    Map<String, com.ahsanfazal.trivia.model.Question> questions = Map.of("test-id", question);
    session.setAttribute("trivia_questions", questions);
  }
}
