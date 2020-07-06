package com.google.sps.data;

import java.util.List;
import java.util.ArrayList;

/** Class to store a career question, which includes a question and several mutiple choice choices*/
public final class CareerQuestionAndChoices {
  private String question;
  private List<CareerQuestionChoice> choices;

  public CareerQuestionAndChoices(String question, List<CareerQuestionChoice> choices) {
    this.question = question;
    this.choices = choices;
  }  

  public String getQuestion() {
    return this.question;
  }

  public List<CareerQuestionChoice> getChoices() {
    return this.choices;
  }
}
