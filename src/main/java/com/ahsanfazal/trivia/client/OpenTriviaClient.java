package com.ahsanfazal.trivia.client;

import com.ahsanfazal.trivia.exception.TriviaApiException;
import com.ahsanfazal.trivia.model.TriviaApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Component
public class OpenTriviaClient {
  private static final Logger logger = LoggerFactory.getLogger(OpenTriviaClient.class);

  private final RestTemplate restTemplate;
  private final String apiUrl;

  public OpenTriviaClient(RestTemplateBuilder builder,
      @Value("${trivia.api.url:https://opentdb.com/api.php}") String apiUrl,
      @Value("${trivia.api.timeout:5000}") int timeout) {
    this.restTemplate = builder
        .setConnectTimeout(Duration.ofMillis(timeout))
        .setReadTimeout(Duration.ofMillis(timeout))
        .build();
    this.apiUrl = apiUrl;
  }

  public TriviaApiResponse fetchQuestions(int amount, String category, String difficulty) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
        .queryParam("amount", amount);

    if (category != null && !category.isEmpty()) {
      builder.queryParam("category", category);
    }

    if (difficulty != null && !difficulty.isEmpty()) {
      builder.queryParam("difficulty", difficulty);
    }

    String url = builder.toUriString();
    logger.info("Fetching questions from: {}", url);

    try {
      ResponseEntity<TriviaApiResponse> response = restTemplate.getForEntity(url, TriviaApiResponse.class);

      if (response.getStatusCode() != HttpStatus.OK) {
        throw new TriviaApiException("Failed to fetch questions. Status: " + response.getStatusCode());
      }

      TriviaApiResponse body = response.getBody();
      if (body == null || body.getResponseCode() != 0) {
        throw new TriviaApiException("Invalid response from trivia API. Response code: " +
            (body != null ? body.getResponseCode() : "null"));
      }

      logger.info("Successfully fetched {} questions", body.getResults().size());
      return body;

    } catch (RestClientException e) {
      logger.error("Error calling trivia API: {}", e.getMessage());
      throw new TriviaApiException("Failed to connect to trivia API", e);
    }
  }
}
