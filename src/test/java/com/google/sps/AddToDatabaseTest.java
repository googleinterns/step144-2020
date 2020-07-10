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
import com.google.sps.servlets.AddToDatabaseServlet;
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


/** Tests that the AddToDatabaseServlet correctly adds static entities to the game datastore */
@RunWith(JUnit4.class)
public final class AddToDatabaseTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private QuestionDatabase questionDatabase;
  private AddToDatabaseServlet addToDatabaseServlet;
  private static final Gson gson = new Gson();
  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final String QUESTION_1 = "A question";
  private static final String CAREER_1 = "career1";
  private static final String CAREER_2 = "career2";
  private static final String CAREER_3 = "career3";
  private static final String CHOICE_1 = "choice1";
  private static final String CHOICE_2 = "choice2";
  private static final String CHOICE_3 = "choice3";
  private static final String CAREER_QUIZ_FORM_SUBMIT_PARAMETER = "careerQuizSubmit";
  private static final String CAREER_QUIZ_QUESTION_PARAMETER = "careerQuestion";
  private static final String CAREER_QUIZ_CHOICETEXT_PARAMETER = "careerChoiceText";
  private static final String CAREER_QUIZ_CAREERPATH_PARAMETER = "careerPath";
  private static final String CAREER_DATABASE_ENTITY_QUERY_STRING = "careerQuizQuestionAndChoices";
  private static final String NUMBER_CHOICES_DONT_MATCH_CAREER_PATHS =
      "The number of choices given is not the same as the number of career paths given.";
  private static final List<QuestionChoice> QUESTION_CHOICES =
      Arrays.asList(
          new QuestionChoice(CHOICE_1, CAREER_1), 
          new QuestionChoice(CHOICE_2, CAREER_2),
          new QuestionChoice(CHOICE_3, CAREER_3));

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    MockitoAnnotations.initMocks(this);
    this.addToDatabaseServlet = new AddToDatabaseServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /**
   * Tests that the doPost method fetches form parameters, creates appropriate career quiz
   * questions, and adds to database
   */
  @Test
  public void testValidInputIsAddedToDatabase() throws IOException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    String questionInput = QUESTION_1;
    String choiceTextInput = CHOICE_1 + ";" + CHOICE_2 + ";" + CHOICE_3;
    String careerPathInput = CAREER_1 + ";" + CAREER_2 + ";" + CAREER_3;

    when(this.response.getWriter()).thenReturn(printWriter);
    mockUserCareerQuestionInput(questionInput, choiceTextInput, careerPathInput);

    this.addToDatabaseServlet.doPost(this.request, this.response);

    QuestionDatabase questionDatabase =
        new QuestionDatabase(datastore, CAREER_DATABASE_ENTITY_QUERY_STRING);
    ArrayList<QuizQuestion> databaseQuestions = questionDatabase.getQuizQuestions();
    Assert.assertEquals(databaseQuestions.size(), 1);

    // converting to JsonElements to do deep equality checks
    JsonElement result = JsonParser.parseString(gson.toJson(databaseQuestions.get(0)));
    JsonElement expected =
        JsonParser.parseString(gson.toJson(new QuizQuestion(QUESTION_1, QUESTION_CHOICES)));
    Assert.assertEquals(result, expected);
  }

  /**
   * Tests that the doPost method fetches form parameters, creates appropriate career quiz
   * questions, and adds to database
   */
  @Test
  public void testInvalidInputTriggersErrorMessage_AndNothingAddedToDatabase() throws IOException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    String questionInput = QUESTION_1;
    String choiceTextInput = CHOICE_1 + ";" + CHOICE_3;
    String careerPathInput = CAREER_1 + ";" + CAREER_2 + ";" + CAREER_3;

    when(this.response.getWriter()).thenReturn(printWriter);
    mockUserCareerQuestionInput(questionInput, choiceTextInput, careerPathInput);

    this.addToDatabaseServlet.doPost(this.request, this.response);

    QuestionDatabase questionDatabase = new QuestionDatabase(datastore, CAREER_DATABASE_ENTITY_QUERY_STRING);
    ArrayList<QuizQuestion> databaseQuestions = questionDatabase.getQuizQuestions();
    // check that nothing has been added to the database
    Assert.assertEquals(databaseQuestions.size(), 0);

    JsonElement result = JsonParser.parseString(stringWriter.toString());
    JsonElement expected = JsonParser.parseString(gson.toJson(
        NUMBER_CHOICES_DONT_MATCH_CAREER_PATHS));
    Assert.assertEquals(result, expected);
  }

  private void mockUserCareerQuestionInput(
      String questionInput, String choiceTextInput, String careerPathInput) {
    when(this.request.getParameter(CAREER_QUIZ_FORM_SUBMIT_PARAMETER))
        .thenReturn(CAREER_QUIZ_FORM_SUBMIT_PARAMETER);
    when(this.request.getParameter(CAREER_QUIZ_QUESTION_PARAMETER)).thenReturn(questionInput);
    when(this.request.getParameter(CAREER_QUIZ_CHOICETEXT_PARAMETER)).thenReturn(choiceTextInput);
    when(this.request.getParameter(CAREER_QUIZ_CAREERPATH_PARAMETER)).thenReturn(careerPathInput);
  }
}
