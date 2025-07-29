package com.ahsanfazal.trivia.exception;

public class SessionExpiredException extends RuntimeException {
  public SessionExpiredException(String message) {
    super(message);
  }
}
