package com.google.sps.data;

import java.util.List;

/**
 * Class to store a question, which includes a string question and several mutiple choice choices of
 * type T
 */
public final class QuestionAndChoices<T> {
  private String question;
  private List<T> choices;

  public QuestionAndChoices(String question, List<T> choices) {
    this.question = question;
    this.choices = choices;
  }

  public String getQuestion() {
    return this.question;
  }

  public List<T> getChoices() {
    return this.choices;
  }
}
