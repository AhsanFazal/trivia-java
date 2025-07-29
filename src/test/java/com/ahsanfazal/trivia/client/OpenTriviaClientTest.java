package com.ahsanfazal.trivia.client;

import com.ahsanfazal.trivia.exception.TriviaApiException;
import com.ahsanfazal.trivia.model.TriviaApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenTriviaClientTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private RestTemplateBuilder restTemplateBuilder;

  private OpenTriviaClient triviaClient;

  @BeforeEach
  void setUp() {
    when(restTemplateBuilder.setConnectTimeout(any())).thenReturn(restTemplateBuilder);
    when(restTemplateBuilder.setReadTimeout(any())).thenReturn(restTemplateBuilder);
    when(restTemplateBuilder.build()).thenReturn(restTemplate);

    triviaClient = new OpenTriviaClient(restTemplateBuilder, "https://opentdb.com/api.php", 5000);
  }

  @Test
  void fetchQuestions_Success() {
    TriviaApiResponse mockResponse = createMockResponse();
    ResponseEntity<TriviaApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

    when(restTemplate.getForEntity(anyString(), eq(TriviaApiResponse.class)))
        .thenReturn(responseEntity);

    TriviaApiResponse response = triviaClient.fetchQuestions(10, null, null);

    assertNotNull(response);
    assertEquals(0, response.getResponseCode());
    assertEquals(1, response.getResults().size());
    assertEquals("What is 2+2?", response.getResults().get(0).getQuestion());
  }

  @Test
  void fetchQuestions_InvalidResponseCode_ThrowsException() {
    TriviaApiResponse mockResponse = new TriviaApiResponse();
    mockResponse.setResponseCode(1);
    ResponseEntity<TriviaApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

    when(restTemplate.getForEntity(anyString(), eq(TriviaApiResponse.class)))
        .thenReturn(responseEntity);

    assertThrows(TriviaApiException.class, () -> {
      triviaClient.fetchQuestions(10, null, null);
    });
  }

  @Test
  void fetchQuestions_ServerError_ThrowsException() {
    ResponseEntity<TriviaApiResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

    when(restTemplate.getForEntity(anyString(), eq(TriviaApiResponse.class)))
        .thenReturn(responseEntity);

    assertThrows(TriviaApiException.class, () -> {
      triviaClient.fetchQuestions(10, null, null);
    });
  }

  @Test
  void fetchQuestions_NetworkError_ThrowsException() {
    when(restTemplate.getForEntity(anyString(), eq(TriviaApiResponse.class)))
        .thenThrow(new RestClientException("Network error"));

    assertThrows(TriviaApiException.class, () -> {
      triviaClient.fetchQuestions(10, null, null);
    });
  }

  @Test
  void fetchQuestions_NullResponse_ThrowsException() {
    ResponseEntity<TriviaApiResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(restTemplate.getForEntity(anyString(), eq(TriviaApiResponse.class)))
        .thenReturn(responseEntity);

    assertThrows(TriviaApiException.class, () -> {
      triviaClient.fetchQuestions(10, null, null);
    });
  }

  private TriviaApiResponse createMockResponse() {
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
}
