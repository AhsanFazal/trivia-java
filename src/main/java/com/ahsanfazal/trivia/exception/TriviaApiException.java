package com.ahsanfazal.trivia.exception;

public class TriviaApiException extends RuntimeException {
  public TriviaApiException(String message) {
    super(message);
  }

  public TriviaApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
