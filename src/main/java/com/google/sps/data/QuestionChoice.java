package com.google.sps.data;

/** parent class that choices for quizzes in application must implement */
public class QuestionChoice {
  private String choiceText;
  private String associatedCareerPath;
  private Boolean isAcceptableChoice;

  public QuestionChoice(
      String choiceText, String associatedCareerPath, Boolean isAcceptableChoice) {
    this.choiceText = choiceText;
    this.associatedCareerPath = associatedCareerPath;
    this.isAcceptableChoice = isAcceptableChoice;
  }

  /** if isAcceptableChoice not provided, assumed to be true */
  public QuestionChoice(String choiceText, String associatedCareerPath) {
    this.choiceText = choiceText;
    this.associatedCareerPath = associatedCareerPath;
    this.isAcceptableChoice = true;
  }

  public String getChoiceText() {
    return this.choiceText;
  }

  public String getAssociatedCareerPath() {
    return this.associatedCareerPath;
  }

  public Boolean getIsAcceptableChoice() {
    return this.isAcceptableChoice;
  }
}
