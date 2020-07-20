package com.google.sps.data;

public class LoggedOutException extends Exception {
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";

  public LoggedOutException() {
    super(LOGGED_OUT_EXCEPTION);
  }
}
