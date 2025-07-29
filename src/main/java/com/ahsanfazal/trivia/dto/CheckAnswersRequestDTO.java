package com.ahsanfazal.trivia.dto;

import java.util.List;

public class CheckAnswersRequestDTO {
  private List<AnswerDTO> answers;

  public CheckAnswersRequestDTO() {
  }

  public CheckAnswersRequestDTO(List<AnswerDTO> answers) {
    this.answers = answers;
  }

  public List<AnswerDTO> getAnswers() {
    return answers;
  }

  public void setAnswers(List<AnswerDTO> answers) {
    this.answers = answers;
  }
}
