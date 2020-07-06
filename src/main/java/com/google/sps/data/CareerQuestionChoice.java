package com.google.sps.data;

/**
 * Class to store the response text of each Choice in the career quiz, and what path selecting it is
 * associated with
 */
public final class CareerQuestionChoice extends QuestionChoice {
  private String associatedCareerPath;

  public CareerQuestionChoice(String choiceText, String associatedCareerPath) {
    super.setChoiceText(choiceText);
    this.associatedCareerPath = associatedCareerPath;
  }

  public String getAssociatedCareerPath() {
    return this.associatedCareerPath;
  }
}
