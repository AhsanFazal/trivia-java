package com.ahsanfazal.trivia.controller;

import com.ahsanfazal.trivia.dto.*;
import com.ahsanfazal.trivia.exception.SessionExpiredException;
import com.ahsanfazal.trivia.exception.TriviaApiException;
import com.ahsanfazal.trivia.service.TriviaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TriviaController.class)
class TriviaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private TriviaService triviaService;

  @Test
  void getQuestions_Success() throws Exception {
    List<QuestionDTO> mockQuestions = Arrays.asList(
        new QuestionDTO("1", "What is 2+2?", Arrays.asList("3", "4", "5", "6"), "Math", "easy"));

    when(triviaService.getQuestions(anyInt(), any(), any())).thenReturn(mockQuestions);

    mockMvc.perform(get("/api/questions")
        .param("amount", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("1"))
        .andExpect(jsonPath("$[0].question").value("What is 2+2?"))
        .andExpect(jsonPath("$[0].options.length()").value(4));
  }

  @Test
  void getQuestions_InvalidAmount_BadRequest() throws Exception {
    mockMvc.perform(get("/api/questions")
        .param("amount", "100"))
        .andExpect(status().is5xxServerError());
  }

  @Test
  void checkAnswers_Success() throws Exception {
    CheckAnswersRequestDTO request = new CheckAnswersRequestDTO(
        Arrays.asList(new AnswerDTO("1", "4")));

    CheckAnswersResponseDTO mockResponse = new CheckAnswersResponseDTO(
        Arrays.asList(new AnswerValidationDTO("1", true, "4")),
        1, 1);

    when(triviaService.checkAnswers(any())).thenReturn(mockResponse);

    mockMvc.perform(post("/api/checkanswers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalQuestions").value(1))
        .andExpect(jsonPath("$.correctAnswers").value(1))
        .andExpect(jsonPath("$.score").value(100.0));
  }

  @Test
  void checkAnswers_EmptyRequest_BadRequest() throws Exception {
    CheckAnswersRequestDTO request = new CheckAnswersRequestDTO(Arrays.asList());

    mockMvc.perform(post("/api/checkanswers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void checkAnswers_SessionExpired_Gone() throws Exception {
    CheckAnswersRequestDTO request = new CheckAnswersRequestDTO(
        Arrays.asList(new AnswerDTO("1", "4")));

    when(triviaService.checkAnswers(any())).thenThrow(new SessionExpiredException("Session expired"));

    mockMvc.perform(post("/api/checkanswers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.error").value("Session Expired"));
  }

  @Test
  void getQuestions_ApiError_ServiceUnavailable() throws Exception {
    when(triviaService.getQuestions(anyInt(), any(), any()))
        .thenThrow(new TriviaApiException("API is down"));

    mockMvc.perform(get("/api/questions"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.error").value("External API Error"));
  }

  @Test
  void health_ReturnsOk() throws Exception {
    mockMvc.perform(get("/api/health"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }
}
