package com.ahsanfazal.trivia.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TriviaApiResponse {
  @JsonProperty("response_code")
  private int responseCode;

  @JsonProperty("results")
  private List<TriviaApiQuestion> results;

  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  public List<TriviaApiQuestion> getResults() {
    return results;
  }

  public void setResults(List<TriviaApiQuestion> results) {
    this.results = results;
  }

  public static class TriviaApiQuestion {
    private String category;
    private String type;
    private String difficulty;
    private String question;

    @JsonProperty("correct_answer")
    private String correctAnswer;

    @JsonProperty("incorrect_answers")
    private List<String> incorrectAnswers;

    public String getCategory() {
      return category;
    }

    public void setCategory(String category) {
      this.category = category;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getDifficulty() {
      return difficulty;
    }

    public void setDifficulty(String difficulty) {
      this.difficulty = difficulty;
    }

    public String getQuestion() {
      return question;
    }

    public void setQuestion(String question) {
      this.question = question;
    }

    public String getCorrectAnswer() {
      return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
      this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
      return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
      this.incorrectAnswers = incorrectAnswers;
    }
  }
}
