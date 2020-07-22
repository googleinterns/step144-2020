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
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.IsFinalStage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
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
public final class IsFinalStageTest {
  // creating a current user for testing
  private static final String NAME = "Fizz";
  private static final String EMAIL = "Fizz@buzz.com";
  private static final String AUTH_DOMAIN = "buzz.com";
  private static final String CURRENT_USER_ID = "boobop";
  private static final String IMAGE_ID = "beepboop";
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
          .setEnvAuthDomain(AUTH_DOMAIN)
          .setEnvEmail(EMAIL)
          .setEnvIsLoggedIn(true)
          .setEnvAttributes(USER_ID_CONFIG);
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private GameStageDatabase gameStageDatabase;
  private PlayerDatabase playerDatabase;
  private IsFinalStage isFinalStage;
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
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String IS_FINAL_STAGE_STRING = "true";
  private static final String IS_NOT_FINAL_STAGE_STRING = "false";

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    MockitoAnnotations.initMocks(this);
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.localUserService = UserServiceFactory.getUserService();
    this.playerDatabase = new PlayerDatabase(localDatastore, localUserService);
    this.gameStageDatabase = new GameStageDatabase(localDatastore);
    this.isFinalStage = new IsFinalStage();
    this.isFinalStage.init();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testThatServletRespondsWithTrue_IfUserOnFinalStage() throws IOException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    User user = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String imageId = IMAGE_ID;
    String gameStageId = PROJECT_MANAGER + LEVEL_3;
    createCurrentPlayerAndAddToDatabase(user, displayName, imageId, gameStageId);

    String stageName = PROJECT_MANAGER_NAME;
    String content = TEST_CONTENT;
    String id = PROJECT_MANAGER + LEVEL_3;
    String quizKey = id;
    boolean isLastStage = true;
    String nextStageId = null;
    createGameStageAndAddToDatabase(stageName, content, id, quizKey, isLastStage, nextStageId);

    this.isFinalStage.doGet(this.request, this.response);

    // checks that isFinalStage returns a string "true"
    String expected = IS_FINAL_STAGE_STRING;
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(expected));
  }

  @Test
  public void testThatServletRespondsWithFalse_IfUserNotOnFinalStage()
      throws IOException, LoggedOutException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    User user = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String imageId = IMAGE_ID;
    String gameStageId = WEB_DEVELOPER + LEVEL_1;
    createCurrentPlayerAndAddToDatabase(user, displayName, imageId, gameStageId);

    String stageName = WEB_DEVELOPER_NAME;
    String content = TEST_CONTENT;
    String id = WEB_DEVELOPER + LEVEL_1;
    String quizKey = id;
    boolean isLastStage = false;
    String nextStageId = WEB_DEVELOPER + LEVEL_2;
    createGameStageAndAddToDatabase(stageName, content, id, quizKey, isLastStage, nextStageId);

    this.isFinalStage.doGet(this.request, this.response);

    // checks that isFinalStage returns a string "true"
    String expected = IS_NOT_FINAL_STAGE_STRING;
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(expected));
  }

  /** Test that logged out player causes LoggedOutException message to be written */
  @Test
  public void isFinalStageWithLoggedOutUser_ExceptionMessageWritten() throws IOException {
    helper.setEnvIsLoggedIn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    this.isFinalStage.doGet(this.request, this.response);

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
}
