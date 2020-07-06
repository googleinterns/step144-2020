package com.google.sps.data;

/** parent class that choices for quizzes in application must implement */
public class QuestionChoice {
  private String choiceText;

  public String getChoiceText() {
    return this.choiceText;
  }

  public void setChoiceText(String choiceText) {
    this.choiceText = choiceText;
  }
}
