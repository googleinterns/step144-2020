package com.google.sps.data;

import java.lang.IllegalArgumentException;
import java.util.HashMap;
import java.util.List;

/** Returns the recommended career path based off the career quiz results. */
public class ProcessPromotionQuizResults {
  private ProcessPromotionQuizResults() {}

  public static Boolean getIsPromotedOrNot(
      List<QuestionChoice> userChoices, double threshold) throws IllegalArgumentException {
    if (userChoices.isEmpty()) {
      throw new IllegalArgumentException();
    }
    double numberAccepted = 0;
    for (QuestionChoice choice : userChoices) {
      if (choice.getIsAcceptableChoice()) {
        numberAccepted = numberAccepted + 1;
      }
    }
    return ((numberAccepted / userChoices.size()) >= threshold);
  }
}
