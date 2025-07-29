package com.ahsanfazal.trivia.dto;

import java.util.List;

public class CheckAnswersResponseDTO {
  private List<AnswerValidationDTO> results;
  private int totalQuestions;
  private int correctAnswers;
  private double score;

  public CheckAnswersResponseDTO() {
  }

  public CheckAnswersResponseDTO(List<AnswerValidationDTO> results, int totalQuestions, int correctAnswers) {
    this.results = results;
    this.totalQuestions = totalQuestions;
    this.correctAnswers = correctAnswers;
    this.score = totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0;
  }

  public List<AnswerValidationDTO> getResults() {
    return results;
  }

  public void setResults(List<AnswerValidationDTO> results) {
    this.results = results;
  }

  public int getTotalQuestions() {
    return totalQuestions;
  }

  public void setTotalQuestions(int totalQuestions) {
    this.totalQuestions = totalQuestions;
  }

  public int getCorrectAnswers() {
    return correctAnswers;
  }

  public void setCorrectAnswers(int correctAnswers) {
    this.correctAnswers = correctAnswers;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }
}
