package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.sps.data.QuestionChoice;
import com.google.sps.data.QuestionDatabase;
import com.google.sps.data.QuizQuestion;
import com.google.sps.servlets.StorePromotionQuestion;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests that StorePromotionQuestion correctly adds static entities to the game datastore */
@RunWith(JUnit4.class)
public final class StorePromotionQuestionTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private QuestionDatabase questionDatabase;
  private StorePromotionQuestion storePromotionQuestion;
  private static final Gson gson = new Gson();
  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final String QUESTION_1 = "A question";
  private static final String QUESTION_2 = "Anotha one";
  private static final String CHOICE_1 = "choice1";
  private static final String CHOICE_2 = "choice2";
  private static final String CHOICE_3 = "choice3";
  private static final String CHOICE_4 = "choice4";
  private static final String CHOICE_5 = "choice5";
  private static final String CHOICE_6 = "choice6";
  private static final String CAREER_PATH = "Web Developer";
  private static final String LEVEL_1 = "1";
  private static final String LEVEL_2 = "2";
  private static final Boolean ACCEPTED_CHOICE = true;
  private static final Boolean NOT_ACCEPTED_CHOICE = false;
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String FORM_SUBMIT_PARAMETER = "promotionQuizSubmit";
  private static final String QUESTION_PARAMETER = "promotionQuestion";
  private static final String CHOICE_TEXT_PARAMETER = "promotionChoiceText";
  private static final String CHOICE_IS_ACCEPTED_PARAMETER = "promotionAcceptedChoice";
  private static final String QUIZ_CAREERPATH_PARAMETER = "promotionCareerPath";
  private static final String QUIZ_LEVEL_PARAMETER = "promotionQuizLevel";
  private static final String ACCEPTED_INPUT = "y";
  private static final String NOT_ACCEPTED_INPUT = "n";
  private static final String INVALID_ACCEPTANCE_SPEC = "true; lol; nope";
  private static final String CANNOT_SPLIT_STRING =
      "Choices and whether they are accepted need to be split by semicolons.";
  private static final String NUMBER_CHOICES_DO_NOT_MATCH_ACCEPTED_SPEC =
      "The specification of the choice options of being accepted or not is the same length as the "
          + "number of choices.";
  private static final String IS_ACCEPTED_INCORRECT_FORMAT =
      "The specification of the choice options of being accepted or not is not given by y or n.";
  private static final List<QuestionChoice> QUESTION_CHOICES_1 =
      Arrays.asList(
          new QuestionChoice(CHOICE_1, CAREER_PATH, ACCEPTED_CHOICE),
          new QuestionChoice(CHOICE_2, CAREER_PATH, NOT_ACCEPTED_CHOICE),
          new QuestionChoice(CHOICE_3, CAREER_PATH, NOT_ACCEPTED_CHOICE));
  private static final List<QuestionChoice> QUESTION_CHOICES_2 =
      Arrays.asList(
          new QuestionChoice(CHOICE_4, CAREER_PATH, ACCEPTED_CHOICE),
          new QuestionChoice(CHOICE_5, CAREER_PATH, NOT_ACCEPTED_CHOICE),
          new QuestionChoice(CHOICE_6, CAREER_PATH, ACCEPTED_CHOICE));

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    MockitoAnnotations.initMocks(this);
    this.storePromotionQuestion = new StorePromotionQuestion();
    this.storePromotionQuestion.init();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /**
   * Tests that the doPost method fetches form parameters, creates appropriate promotion quiz
   * questions, and adds to database
   */
  @Test
  public void doPost_withOneValidInput_addsToDatabase() throws IOException {
    // mock that the user has entered the following as input into the promotion question form
    String questionInput = QUESTION_1;
    String choiceTextInput = CHOICE_1 + ";" + CHOICE_2 + ";" + CHOICE_3;
    String isAcceptedChoiceInput =
        ACCEPTED_INPUT + ";" + NOT_ACCEPTED_INPUT + ";" + NOT_ACCEPTED_INPUT;
    String careerPathInput = CAREER_PATH;
    String levelInput = LEVEL_1;

    mockUserPromotionQuestionInput(
        questionInput, choiceTextInput, isAcceptedChoiceInput, careerPathInput, levelInput);

    this.storePromotionQuestion.doPost(this.request, this.response);

    // the question should be found with the quiz key created by career path + level
    String expectedEntityQueryString = CAREER_PATH + LEVEL_1;
    QuestionDatabase questionDatabase =
        new QuestionDatabase(this.datastore, expectedEntityQueryString);
    ArrayList<QuizQuestion> databaseQuestions = questionDatabase.getQuizQuestions();
    // only the one promotion question should be found
    Assert.assertEquals(databaseQuestions.size(), 1);

    // converting to JsonElements to do deep equality checks
    JsonElement result = JsonParser.parseString(gson.toJson(databaseQuestions.get(0)));
    JsonElement expected =
        JsonParser.parseString(gson.toJson(new QuizQuestion(QUESTION_1, QUESTION_CHOICES_1)));
    Assert.assertEquals(result, expected);
  }

  @Test
  public void doPost_withTwoValidInputs_WithDifferentGameStages_addsToDatabase()
      throws IOException {
    // mock that the user has entered the following as input into the promotion question form
    String questionInput = QUESTION_1;
    String choiceTextInput = CHOICE_1 + ";" + CHOICE_2 + ";" + CHOICE_3;
    String isAcceptedChoiceInput =
        ACCEPTED_INPUT + ";" + NOT_ACCEPTED_INPUT + ";" + NOT_ACCEPTED_INPUT;
    String careerPathInput = CAREER_PATH;
    String levelInput = LEVEL_1;

    mockUserPromotionQuestionInput(
        questionInput, choiceTextInput, isAcceptedChoiceInput, careerPathInput, levelInput);

    this.storePromotionQuestion.doPost(this.request, this.response);

    // the question should be found with the quiz key created by career path + level
    String expectedEntityQueryString1 = CAREER_PATH + LEVEL_1;
    QuestionDatabase questionDatabase1 =
        new QuestionDatabase(this.datastore, expectedEntityQueryString1);
    ArrayList<QuizQuestion> databaseQuestions1 = questionDatabase1.getQuizQuestions();

    // now add promotion question for different game stage. Check that previous database stays the
    // same and new one is updated correctly

    String questionInput2 = QUESTION_2;
    String choiceTextInput2 = CHOICE_4 + ";" + CHOICE_5 + ";" + CHOICE_6;
    String isAcceptedChoiceInput2 =
        ACCEPTED_INPUT + ";" + NOT_ACCEPTED_INPUT + ";" + ACCEPTED_INPUT;
    String careerPathInput2 = CAREER_PATH;
    String levelInput2 = LEVEL_2;

    mockUserPromotionQuestionInput(
        questionInput2, choiceTextInput2, isAcceptedChoiceInput2, careerPathInput2, levelInput2);

    this.storePromotionQuestion.doPost(this.request, this.response);

    // the question should be found with the quiz key created by career path + level
    String expectedEntityQueryString2 = CAREER_PATH + LEVEL_2;
    QuestionDatabase questionDatabase2 =
        new QuestionDatabase(this.datastore, expectedEntityQueryString2);
    ArrayList<QuizQuestion> databaseQuestions2 = questionDatabase2.getQuizQuestions();
    // only the one promotion question should be found with new game stage
    Assert.assertEquals(databaseQuestions2.size(), 1);
    // only one promotion question should still be found in old game stage
    Assert.assertEquals(databaseQuestions1.size(), 1);

    // converting to JsonElements to do deep equality checks for both game stage elements
    JsonElement result1 = JsonParser.parseString(gson.toJson(databaseQuestions1.get(0)));
    JsonElement expected1 =
        JsonParser.parseString(gson.toJson(new QuizQuestion(QUESTION_1, QUESTION_CHOICES_1)));
    Assert.assertEquals(result1, expected1);

    JsonElement result2 = JsonParser.parseString(gson.toJson(databaseQuestions2.get(0)));
    JsonElement expected2 =
        JsonParser.parseString(gson.toJson(new QuizQuestion(QUESTION_2, QUESTION_CHOICES_2)));
    Assert.assertEquals(result2, expected2);
  }

  /**
   * Tests that if input is in invalid format, and error message is sent as a response and nothing
   * is added to the database
   */
  @Test
  public void testNumberChoicesNotConsistent_TriggersErrorMessage_AndNothingAddedToDatabase()
      throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    // input where specification of accepted choices does not match number of choices given
    String questionInput = QUESTION_1;
    String choiceTextInput = CHOICE_1 + ";" + CHOICE_2 + ";" + CHOICE_3;
    String isAcceptedChoiceInput = ACCEPTED_INPUT + ";" + NOT_ACCEPTED_INPUT;
    String careerPathInput = CAREER_PATH;
    String levelInput = LEVEL_1;

    mockUserPromotionQuestionInput(
        questionInput, choiceTextInput, isAcceptedChoiceInput, careerPathInput, levelInput);

    this.storePromotionQuestion.doPost(this.request, this.response);

    String expectedEntityQueryString = CAREER_PATH + LEVEL_1;
    QuestionDatabase questionDatabase = new QuestionDatabase(datastore, expectedEntityQueryString);
    ArrayList<QuizQuestion> databaseQuestions = questionDatabase.getQuizQuestions();
    // check that nothing has been added to the database
    Assert.assertEquals(databaseQuestions.size(), 0);

    JsonElement result = JsonParser.parseString(stringWriter.toString());
    JsonElement expected =
        JsonParser.parseString(gson.toJson(NUMBER_CHOICES_DO_NOT_MATCH_ACCEPTED_SPEC));
    Assert.assertEquals(result, expected);
  }

  @Test
  public void incorrectYNFormatForChoiceAcceptance_TriggersErrorMessage_AndNothingAddedToDatabase()
      throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    // input where specification of accepted choices does not match number of choices given
    String questionInput = QUESTION_1;
    String choiceTextInput = CHOICE_1 + ";" + CHOICE_2 + ";" + CHOICE_3;
    String isAcceptedChoiceInput = INVALID_ACCEPTANCE_SPEC;
    String careerPathInput = CAREER_PATH;
    String levelInput = LEVEL_1;

    mockUserPromotionQuestionInput(
        questionInput, choiceTextInput, isAcceptedChoiceInput, careerPathInput, levelInput);

    this.storePromotionQuestion.doPost(this.request, this.response);

    String expectedEntityQueryString = CAREER_PATH + LEVEL_1;
    QuestionDatabase questionDatabase = new QuestionDatabase(datastore, expectedEntityQueryString);
    ArrayList<QuizQuestion> databaseQuestions = questionDatabase.getQuizQuestions();
    // check that nothing has been added to the database
    Assert.assertEquals(databaseQuestions.size(), 0);

    JsonElement result = JsonParser.parseString(stringWriter.toString());
    JsonElement expected = JsonParser.parseString(gson.toJson(IS_ACCEPTED_INCORRECT_FORMAT));
    Assert.assertEquals(result, expected);
  }

  private void mockUserPromotionQuestionInput(
      String questionInput,
      String choiceTextInput,
      String isAcceptedChoiceInput,
      String careerPathInput,
      String levelInput) {
    when(this.request.getParameter(FORM_SUBMIT_PARAMETER)).thenReturn(FORM_SUBMIT_PARAMETER);
    when(this.request.getParameter(QUESTION_PARAMETER)).thenReturn(questionInput);
    when(this.request.getParameter(CHOICE_TEXT_PARAMETER)).thenReturn(choiceTextInput);
    when(this.request.getParameter(CHOICE_IS_ACCEPTED_PARAMETER)).thenReturn(isAcceptedChoiceInput);
    when(this.request.getParameter(QUIZ_CAREERPATH_PARAMETER)).thenReturn(careerPathInput);
    when(this.request.getParameter(QUIZ_LEVEL_PARAMETER)).thenReturn(levelInput);
  }
}
