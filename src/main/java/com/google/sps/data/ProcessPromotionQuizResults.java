package com.google.sps.data;

import java.util.List;

/** Returns the recommended career path based off the career quiz results. */
public class ProcessPromotionQuizResults {
  private static final String EMPTY_CHOICES_EXCEPTION =
      "Empty list of user choices entered. Cannot process empty list.";

  private ProcessPromotionQuizResults() {}

  public static Boolean isPromoted(List<QuestionChoice> userChoices, double threshold)
      throws IllegalArgumentException {
    if (userChoices.isEmpty()) {
      throw new IllegalArgumentException(EMPTY_CHOICES_EXCEPTION);
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
