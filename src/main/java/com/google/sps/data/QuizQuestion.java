package com.google.sps.data;

import java.util.List;

/**
 * Class to store a question, which includes a string question and several mutiple choice choices
 */
public final class QuizQuestion {
  private String question;
  private List<QuestionChoice> choices;

  public QuizQuestion(String question, List<QuestionChoice> choices) {
    this.question = question;
    this.choices = choices;
  }

  public String getQuestion() {
    return this.question;
  }

  public List<QuestionChoice> getChoices() {
    return this.choices;
  }
}
