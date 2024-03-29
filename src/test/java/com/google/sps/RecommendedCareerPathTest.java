package com.google.sps;

import com.google.sps.data.ProcessCareerQuizResults;
import com.google.sps.data.QuestionChoice;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests that the submission of choices for the CareerQuizServlet returns recommended path that is
 * associated to the majority of choices.
 */
@RunWith(JUnit4.class)
public final class RecommendedCareerPathTest {

  private static final String CAREER_1 = "Web Developer";
  private static final String CAREER_2 = "Program Manager";
  private static final String CAREER_3 = "Software Engineer";
  private static final String CHOICE_1 = "choice1";
  private static final String CHOICE_2 = "choice2";
  private static final String CHOICE_3 = "choice3";
  private static final String CHOICE_4 = "choice4";
  private static final String CHOICE_5 = "choice5";

  @Test
  public void outputsMostFrequentCareerPath_2choices() {
    List<QuestionChoice> userChoices =
        Arrays.asList(
            new QuestionChoice(CHOICE_1, CAREER_1),
            new QuestionChoice(CHOICE_2, CAREER_2),
            new QuestionChoice(CHOICE_3, CAREER_2),
            new QuestionChoice(CHOICE_4, CAREER_3));
    String expected = CAREER_2;
    String result = ProcessCareerQuizResults.getRecommendedCareerPath(userChoices);
    Assert.assertEquals(result, expected);
  }

  @Test
  public void picksArbitraryCareerPath_forTies() {
    List<QuestionChoice> userChoices =
        Arrays.asList(
            new QuestionChoice(CHOICE_1, CAREER_1),
            new QuestionChoice(CHOICE_2, CAREER_1),
            new QuestionChoice(CHOICE_3, CAREER_2),
            new QuestionChoice(CHOICE_4, CAREER_2),
            new QuestionChoice(CHOICE_5, CAREER_3));
    String result = ProcessCareerQuizResults.getRecommendedCareerPath(userChoices);
    String expectedOption1 = CAREER_1;
    String expectedOption2 = CAREER_2;
    Assert.assertTrue(result.equals(expectedOption1) || result.equals(expectedOption2));
  }

  @Test
  public void ifChoicesAreEmpty_OutputsEmptyString() {
    List<QuestionChoice> userChoices = Arrays.asList();
    String expected = "";
    String result = ProcessCareerQuizResults.getRecommendedCareerPath(userChoices);
    Assert.assertEquals(result, expected);
  }
}
