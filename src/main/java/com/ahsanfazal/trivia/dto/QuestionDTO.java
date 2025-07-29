package com.ahsanfazal.trivia.dto;

import java.util.List;

public class QuestionDTO {
  private String id;
  private String question;
  private List<String> options;
  private String category;
  private String difficulty;

  public QuestionDTO() {
  }

  public QuestionDTO(String id, String question, List<String> options, String category, String difficulty) {
    this.id = id;
    this.question = question;
    this.options = options;
    this.category = category;
    this.difficulty = difficulty;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public List<String> getOptions() {
    return options;
  }

  public void setOptions(List<String> options) {
    this.options = options;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }
}
