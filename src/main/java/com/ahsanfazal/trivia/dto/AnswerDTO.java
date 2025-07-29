package com.ahsanfazal.trivia.dto;

public class AnswerDTO {
  private String questionId;
  private String answer;

  public AnswerDTO() {
  }

  public AnswerDTO(String questionId, String answer) {
    this.questionId = questionId;
    this.answer = answer;
  }

  public String getQuestionId() {
    return questionId;
  }

  public void setQuestionId(String questionId) {
    this.questionId = questionId;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }
}
