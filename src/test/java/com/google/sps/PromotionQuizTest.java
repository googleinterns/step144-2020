package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.sps.data.PromotionMessage;
import com.google.sps.data.QuestionChoice;
import com.google.sps.data.QuestionDatabase;
import com.google.sps.data.QuizQuestion;
import com.google.sps.servlets.PromotionQuizServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

/** Tests the PromotionQuiz servlet and its interactions with QuestionDatabase */
@RunWith(JUnit4.class)
public final class PromotionQuizTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private QuestionDatabase questionDatabase;
  private PromotionQuizServlet promotionQuizServlet;
  private static final Gson gson = new Gson();
  private static final String QUESTION = "A question";
  private static final String CAREER_1 = "career1";
  private static final String CAREER_2 = "career2";
  private static final String CHOICE_1 = "choice1";
  private static final String CHOICE_2 = "choice2";
  private static final Boolean ACCEPTABLE = true;
  private static final Boolean NOT_ACCEPTABLE = false;
  private static final String PROMOTION_QUIZ_QUERY = "test1level1";
  private static final String PROMOTED_MESSAGE =
      "Congratulations, you passed the quiz and were promoted!";
  private static final String NOT_PROMOTED_MESSAGE =
      "You did not pass the quiz. Study the content and try again later";
  private static final PromotionMessage IS_PROMOTED =
      new PromotionMessage(/*isPromoted = */ true, PROMOTED_MESSAGE);
  private static final PromotionMessage IS_NOT_PROMOTED =
      new PromotionMessage(/*isPromoted = */ false, NOT_PROMOTED_MESSAGE);
  private static final List<QuestionChoice> HARD_CODED_CHOICES =
      Arrays.asList(
          new QuestionChoice(CHOICE_1, CAREER_1, ACCEPTABLE),
          new QuestionChoice(CHOICE_2, CAREER_2, NOT_ACCEPTABLE));
  private static final String DATABASE_OBJECT_JSON =
      "[{\"question\":\"A question\",\"choices\":"
          + "[{\"choiceText\":\"choice1\",\"associatedCareerPath\":\"career1\","
          + "\"isAcceptableChoice\":true},"
          + "{\"choiceText\":\"choice2\",\"associatedCareerPath\":\"career2\","
          + "\"isAcceptableChoice\":false}]}]";

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    MockitoAnnotations.initMocks(this);
    this.promotionQuizServlet = this.createPromotionQuizServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private PromotionQuizServlet createPromotionQuizServlet() {
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    QuestionDatabase questionDatabase = new QuestionDatabase(localDatastore, PROMOTION_QUIZ_QUERY);
    questionDatabase.putQuizQuestionsIntoDatabase(new QuizQuestion(QUESTION, HARD_CODED_CHOICES));
    PromotionQuizServlet promotionQuizServlet = new PromotionQuizServlet();
    promotionQuizServlet.setQuestionDatabase(questionDatabase);
    return promotionQuizServlet;
  }

  /**
   * Tests that the doGet method returns JSON containing database queried promotion questions and
   * choices
   */
  @Test
  public void testPromotionQuizServlet_OutputsJsonDatabaseQuizQuestion() throws IOException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    // mocks the result of querying the QuestionDatabase for all the Promotion Question and
    // choices
    this.promotionQuizServlet.doGet(this.request, this.response);
    // checks that the string writer used in servlet mock response contains the database object JSON
    // that matches with the hardcoded QuizQuestion given be the mock database
    JsonElement expected = JsonParser.parseString(DATABASE_OBJECT_JSON);
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(result, expected);
  }

  /** Tests that the doPost methods responds with being promoted or not */
  @Test
  public void testPostMethodRespondsWith_Promoted() throws IOException {
    // mocks user behavior of selecting an accepted choice
    String choiceJson = gson.toJson(new QuestionChoice(CHOICE_1, CAREER_1, ACCEPTABLE));
    when(this.request.getParameter(QUESTION)).thenReturn(choiceJson);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(writer);
    this.promotionQuizServlet.doPost(this.request, this.response);
    JsonElement expected = JsonParser.parseString(gson.toJson(IS_PROMOTED));
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(result, expected);
  }

  @Test
  public void testPostMethodRespondsWith_NotPromoted() throws IOException {
    // mocks user behavior of selecting a not accepted choice
    String choiceJson = gson.toJson(new QuestionChoice(CHOICE_1, CAREER_1, NOT_ACCEPTABLE));
    when(this.request.getParameter(QUESTION)).thenReturn(choiceJson);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(writer);
    this.promotionQuizServlet.doPost(this.request, this.response);
    JsonElement expected = JsonParser.parseString(gson.toJson(IS_NOT_PROMOTED));
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(result, expected);
  }
}
