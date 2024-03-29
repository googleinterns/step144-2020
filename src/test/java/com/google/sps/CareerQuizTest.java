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
import com.google.sps.servlets.CareerQuizServlet;
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

/** Tests the CareerQuiz servlet and its interactions with QuestionDatabase */
@RunWith(JUnit4.class)
public final class CareerQuizTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private QuestionDatabase questionDatabase;
  private CareerQuizServlet careerQuizServlet;
  private static final Gson gson = new Gson();
  private static final String CAREER_1 = "career1";
  private static final String CAREER_2 = "career2";
  private static final String CHOICE_1 = "choice1";
  private static final String CHOICE_2 = "choice2";
  private static final String ENTITY_QUERY_STRING = "careerQuizQuestionAndChoices";
  private static final List<QuestionChoice> HARD_CODED_CHOICES =
      Arrays.asList(new QuestionChoice(CHOICE_1, CAREER_1), new QuestionChoice(CHOICE_2, CAREER_2));
  private static final String DATABASE_OBJECT_JSON =
      "[{\"question\":\"A question\",\"choices\":"
          + "[{\"choiceText\":\"choice1\",\"associatedCareerPath\":\"career1\","
          + "\"isAcceptableChoice\":true},"
          + "{\"choiceText\":\"choice2\",\"associatedCareerPath\":\"career2\","
          + "\"isAcceptableChoice\":true}]}]";

  private static final String QUESTION = "A question";

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    MockitoAnnotations.initMocks(this);
    this.careerQuizServlet = this.createCareerQuestionServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private CareerQuizServlet createCareerQuestionServlet() {
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    questionDatabase = new QuestionDatabase(localDatastore, ENTITY_QUERY_STRING);
    questionDatabase.putQuizQuestionsIntoDatabase(new QuizQuestion(QUESTION, HARD_CODED_CHOICES));
    CareerQuizServlet careerQuizServlet = new CareerQuizServlet();
    return careerQuizServlet;
  }

  /**
   * Tests that the doGet method returns JSON containing database queried career question and
   * choices
   */
  @Test
  public void testCareerQuizServlet_OutputsJsonDatabaseQuizQuestion() throws IOException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    // mocks the result of querying the QuestionDatabase for all the Career Question and
    // choices
    this.careerQuizServlet.doGet(this.request, this.response);
    // checks that the string writer used in servlet mock response contains the database object JSON
    // that matches with the hardcoded CareerQAndChoice given be the mock database
    JsonElement expected = JsonParser.parseString(DATABASE_OBJECT_JSON);
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(result, expected);
  }

  /** Tests that the doPost methods responds with the recommended String career path */
  @Test
  public void testPostMethodRespondsWith_RecommendedCareerPath() throws IOException {
    // mocks user behavior of selecting the choice 2 radio button on the career quiz
    String choice2_json = gson.toJson(new QuestionChoice(CHOICE_2, CAREER_2));
    when(this.request.getParameter(QUESTION)).thenReturn(choice2_json);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(writer);
    this.careerQuizServlet.doPost(this.request, this.response);
    String expected = CAREER_2;
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(expected));
  }
}
