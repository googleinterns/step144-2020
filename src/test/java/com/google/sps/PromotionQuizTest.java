package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.data.QuestionChoice;
import com.google.sps.data.QuestionDatabase;
import com.google.sps.data.QuizQuestion;
import com.google.sps.servlets.PromotionQuizServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  // creating a current user for testing
  private static final String NAME = "Linda";
  private static final String EMAIL = "Linda@email.com";
  private static final String AUTH_DOMAIN = "email.com";
  private static final String CURRENT_USER_ID = "testidme";
  private static final String IMAGE_ID = "imageId";
  private static final String USER_ID_KEY_PATH =
      "com.google.appengine.api.users.UserService.user_id_key";
  private static Map<String, Object> USER_ID_CONFIG =
      new HashMap<String, Object>() {
        {
          put(USER_ID_KEY_PATH, CURRENT_USER_ID);
        }
      };
  // creating a test user service, setting current user to be a logged in admin
  private final LocalUserServiceTestConfig localUserServiceTestConfig =
      new LocalUserServiceTestConfig().setOAuthUserId(CURRENT_USER_ID);
  private final LocalDatastoreServiceTestConfig localDatastoreServiceTestConfig =
      new LocalDatastoreServiceTestConfig();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(localUserServiceTestConfig, localDatastoreServiceTestConfig)
          .setEnvIsAdmin(true)
          .setEnvAuthDomain(AUTH_DOMAIN)
          .setEnvEmail(EMAIL)
          .setEnvIsLoggedIn(true)
          .setEnvAttributes(USER_ID_CONFIG);
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private QuestionDatabase questionDatabase;
  private GameStageDatabase gameStageDatabase;
  private PlayerDatabase playerDatabase;
  private PromotionQuizServlet promotionQuizServlet;
  private DatastoreService localDatastore;
  private UserService localUserService;
  private static final Gson gson = new Gson();
  private static final String PROJECT_MANAGER = "Project Manager";
  private static final String PROJECT_MANAGER_NAME = "Project Manager Name";
  private static final String WEB_DEVELOPER = "Web Developer";
  private static final String WEB_DEVELOPER_NAME = "Web Developer Name";
  private static final String TEST_CONTENT = "test content";
  private static final String LEVEL_1 = "1";
  private static final String LEVEL_2 = "2";
  private static final String LEVEL_3 = "3";
  private static final String QUESTION = "A question";
  private static final String CAREER_1 = "career1";
  private static final String CAREER_2 = "career2";
  private static final String CHOICE_1 = "choice1";
  private static final String CHOICE_2 = "choice2";
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final Boolean ACCEPTABLE = true;
  private static final Boolean NOT_ACCEPTABLE = false;
  private static final String PROMOTION_QUIZ_QUERY = "test1level1";
  private static final String PROMOTED_MESSAGE =
      "Congratulations, you passed the quiz and were promoted!";
  private static final String NOT_PROMOTED_MESSAGE =
      "You did not pass the quiz. Study the content and try again later";
  private static final String IS_FINAL_STAGE_MESSAGE =
      "Congratulations! You reached the final stage! You may no longer be promoted in this path.";
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
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.localUserService = UserServiceFactory.getUserService();
    this.playerDatabase = new PlayerDatabase(localDatastore, localUserService);
    this.gameStageDatabase = new GameStageDatabase(localDatastore);
    this.promotionQuizServlet = this.createPromotionQuizServlet();
    this.promotionQuizServlet.init();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private PromotionQuizServlet createPromotionQuizServlet() {
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    PromotionQuizServlet promotionQuizServlet = new PromotionQuizServlet();
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

    QuizQuestion quizQuestion = new QuizQuestion(QUESTION, HARD_CODED_CHOICES);
    String quizQueryString = PROJECT_MANAGER + LEVEL_1;
    putQuizQuestionIntoDatbase(quizQuestion, quizQueryString);

    User user = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String imageId = IMAGE_ID;
    String gameStageId = PROJECT_MANAGER + LEVEL_1;
    createCurrentPlayerAndAddToDatabase(user, displayName, imageId, gameStageId);

    String stageName = PROJECT_MANAGER_NAME;
    String content = TEST_CONTENT;
    String id = PROJECT_MANAGER + LEVEL_1;
    String quizKey = id;
    boolean isLastStage = false;
    String nextStageId = PROJECT_MANAGER + LEVEL_2;
    createGameStageAndAddToDatabase(stageName, content, id, quizKey, isLastStage, nextStageId);

    // mocks the result of querying the QuestionDatabase for all the Promotion Question and
    // choices
    this.promotionQuizServlet.doGet(this.request, this.response);
    // checks that the string writer used in servlet mock response contains the database object JSON
    // that matches with the hardcoded QuizQuestion given be the mock database
    JsonElement expected = JsonParser.parseString(DATABASE_OBJECT_JSON);
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(result, expected);
  }

  /** If user is on final stage of a path, doGet outputs a message instead of questions */
  @Test
  public void testGetMethodResponds_WithMessageNotQuestion_ifOnFinalStage()
      throws IOException, LoggedOutException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(writer);

    User user = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String imageId = IMAGE_ID;
    String gameStageId = WEB_DEVELOPER + LEVEL_3;
    createCurrentPlayerAndAddToDatabase(user, displayName, imageId, gameStageId);

    String stageName = WEB_DEVELOPER_NAME;
    String content = TEST_CONTENT;
    String id = WEB_DEVELOPER + LEVEL_3;
    String quizKey = id;
    boolean isLastStage = true;
    String nextStageId = null;
    createGameStageAndAddToDatabase(stageName, content, id, quizKey, isLastStage, nextStageId);

    this.promotionQuizServlet.doGet(this.request, this.response);

    JsonElement expected = JsonParser.parseString(gson.toJson(IS_FINAL_STAGE_MESSAGE));
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(result, expected);
  }

  /** Tests that the doPost methods responds with being promoted or not */
  @Test
  public void testPostMethodRespondsWith_Promoted() throws IOException, LoggedOutException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(writer);

    QuizQuestion quizQuestion = new QuizQuestion(QUESTION, HARD_CODED_CHOICES);
    String quizQueryString = WEB_DEVELOPER + LEVEL_2;
    putQuizQuestionIntoDatbase(quizQuestion, quizQueryString);

    User user = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String imageId = IMAGE_ID;
    String gameStageId = WEB_DEVELOPER + LEVEL_2;
    createCurrentPlayerAndAddToDatabase(user, displayName, imageId, gameStageId);

    String stageName = WEB_DEVELOPER_NAME;
    String content = TEST_CONTENT;
    String id = WEB_DEVELOPER + LEVEL_2;
    String quizKey = id;
    boolean isLastStage = false;
    String nextStageId = WEB_DEVELOPER + LEVEL_3;
    createGameStageAndAddToDatabase(stageName, content, id, quizKey, isLastStage, nextStageId);

    // mocks user behavior of selecting an accepted choice
    String choiceJson = gson.toJson(new QuestionChoice(CHOICE_1, CAREER_1, ACCEPTABLE));
    when(this.request.getParameter(QUESTION)).thenReturn(choiceJson);

    this.promotionQuizServlet.doPost(this.request, this.response);

    // checks that user game stage is updated to next level
    String expectedGameStageId = WEB_DEVELOPER + LEVEL_3;
    String resultGameStageId = this.playerDatabase.getEntityCurrentPageID();
    Assert.assertEquals(expectedGameStageId, resultGameStageId);
  }

  @Test
  public void testPostMethodRespondsWith_NotPromoted() throws IOException, LoggedOutException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(writer);

    QuizQuestion quizQuestion = new QuizQuestion(QUESTION, HARD_CODED_CHOICES);
    String quizQueryString = PROJECT_MANAGER + LEVEL_2;
    putQuizQuestionIntoDatbase(quizQuestion, quizQueryString);

    User user = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String imageId = IMAGE_ID;
    String gameStageId = PROJECT_MANAGER + LEVEL_2;
    createCurrentPlayerAndAddToDatabase(user, displayName, imageId, gameStageId);

    String stageName = PROJECT_MANAGER_NAME;
    String content = TEST_CONTENT;
    String id = PROJECT_MANAGER + LEVEL_2;
    String quizKey = id;
    boolean isLastStage = false;
    String nextStageId = PROJECT_MANAGER + LEVEL_3;
    createGameStageAndAddToDatabase(stageName, content, id, quizKey, isLastStage, nextStageId);

    // mocks user behavior of selecting a not accepted choice
    String choiceJson = gson.toJson(new QuestionChoice(CHOICE_1, CAREER_1, NOT_ACCEPTABLE));
    when(this.request.getParameter(QUESTION)).thenReturn(choiceJson);

    this.promotionQuizServlet.doPost(this.request, this.response);

    // checks that user game stage is *not* updated to next level
    String expectedGameStageId = PROJECT_MANAGER + LEVEL_2;
    String resultGameStageId = this.playerDatabase.getEntityCurrentPageID();
    Assert.assertEquals(expectedGameStageId, resultGameStageId);
  }

  /** Test that logged out player causes LoggedOutException message to be written */
  @Test
  public void getPromotionQuizWithLoggedOutUser_ExceptionMessageWritten() throws IOException {
    helper.setEnvIsLoggedIn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    this.promotionQuizServlet.doGet(this.request, this.response);

    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(LOGGED_OUT_EXCEPTION));
  }

  private void createCurrentPlayerAndAddToDatabase(
      User currentUser, String displayName, String imageId, String currentGameStageId) {
    Player player =
        new Player(
            displayName,
            currentUser.getEmail(),
            currentUser.getUserId(),
            imageId,
            currentGameStageId);
    this.playerDatabase.addPlayerToDatabase(player);
  }

  private void createGameStageAndAddToDatabase(
      String stageName,
      String content,
      String id,
      String quizKey,
      boolean isLastStage,
      String nextStageId) {
    GameStage newGameStage =
        new GameStage(stageName, content, id, quizKey, isLastStage, nextStageId);
    this.gameStageDatabase.storeGameStage(newGameStage);
  }

  private void putQuizQuestionIntoDatbase(QuizQuestion quizQuestion, String quizKey) {
    this.questionDatabase = new QuestionDatabase(localDatastore, quizKey);
    questionDatabase.putQuizQuestionsIntoDatabase(quizQuestion);
  }
}
