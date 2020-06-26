package com.google.sps.data;

import java.util.List;
import java.util.ArrayList;

/** Class to store the reponse text of each Choice in the career quiz, and what path
    selecting it is associated with */
public final class CareerQuestionChoice {
  private String choiceText;
  private String associatedCareerPath;

  public CareerQuestionChoice(String choiceText, String associatedCareerPath) {
    this.choiceText = choiceText;
    this.associatedCareerPath= associatedCareerPath;
  }

  public String getAssociatedCareerPath() {
    return this.associatedCareerPath;
  }

  public String getChoiceText() {
    return this.choiceText;
  }
}
