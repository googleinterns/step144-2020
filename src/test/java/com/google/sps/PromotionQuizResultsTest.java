package com.google.sps;

import com.google.sps.data.ProcessPromotionQuizResults;
import com.google.sps.data.QuestionChoice;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests that the submission of choices for the CareerQuizServlet returns recommended path that is
 * associated to the majority of choices.
 */
@RunWith(JUnit4.class)
public final class PromotionQuizResultsTest {

  private static final String CAREER_1 = "Web Developer";
  private static final String CAREER_2 = "Program Manager";
  private static final String CAREER_3 = "Software Engineer";
  private static final String CHOICE_1 = "choice1";
  private static final String CHOICE_2 = "choice2";
  private static final String CHOICE_3 = "choice3";
  private static final String CHOICE_4 = "choice4";
  private static final String CHOICE_5 = "choice5";
  private static final String EMPTY_CHOICES_EXCEPTION =
      "Empty list of user choices entered. Cannot process empty list.";
  private static final Boolean ACCEPTABLE_CHOICE = true;
  private static final Boolean NOT_ACCEPTABLE_CHOICE = false;

  @Test
  public void proportionAcceptableChoicesAreOverThreshold_GetsPromoted() {
    List<QuestionChoice> userChoices =
        Arrays.asList(
            new QuestionChoice(CHOICE_1, CAREER_1, ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_2, CAREER_2, ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_3, CAREER_2, NOT_ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_4, CAREER_3, ACCEPTABLE_CHOICE));
    double threshold = 0.6;
    Boolean expected = true;
    Boolean result = ProcessPromotionQuizResults.isPromoted(userChoices, threshold);
    Assert.assertEquals(result, expected);
  }

  @Test
  public void proportionAcceptableChoicesEqualThreshold_GetsPromoted() {
    List<QuestionChoice> userChoices =
        Arrays.asList(
            new QuestionChoice(CHOICE_1, CAREER_1, ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_2, CAREER_2, ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_3, CAREER_2, NOT_ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_4, CAREER_3, NOT_ACCEPTABLE_CHOICE));
    double threshold = 0.5;
    Boolean expected = true;
    Boolean result = ProcessPromotionQuizResults.isPromoted(userChoices, threshold);
    Assert.assertEquals(result, expected);
  }

  @Test
  public void proportionAcceptableChoicesAreUnderThreshold_DoesNotGetPromoted() {
    List<QuestionChoice> userChoices =
        Arrays.asList(
            new QuestionChoice(CHOICE_1, CAREER_1, NOT_ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_2, CAREER_2, NOT_ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_3, CAREER_2, NOT_ACCEPTABLE_CHOICE),
            new QuestionChoice(CHOICE_4, CAREER_3, ACCEPTABLE_CHOICE));
    double threshold = 0.4;
    Boolean expected = false;
    Boolean result = ProcessPromotionQuizResults.isPromoted(userChoices, threshold);
    Assert.assertEquals(result, expected);
  }

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void ifChoicesAreEmpty_ThrowsIllegalArgumentException() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage(EMPTY_CHOICES_EXCEPTION);
    List<QuestionChoice> userChoices = Arrays.asList();
    double threshold = 0.5;
    Boolean result = ProcessPromotionQuizResults.isPromoted(userChoices, threshold);
  }
}
