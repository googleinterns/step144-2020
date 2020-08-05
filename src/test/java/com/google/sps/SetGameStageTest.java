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
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.SetGameStage;
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

/** Tests that the SetGameStage servlet correctly updates game stage to be an admin */
@RunWith(JUnit4.class)
public final class SetGameStageTest {
  private static final String AUTH_DOMAIN = "email.com";
  private static final String CURR_USER_ID = "testid";
  private static final String USER_ID_KEY_PATH =
      "com.google.appengine.api.users.UserService.user_id_key";
  private static Map<String, Object> USER_ID_CONFIG =
      new HashMap<String, Object>() {
        {
          put(USER_ID_KEY_PATH, CURR_USER_ID);
        }
      };
  // creating a test user service, setting current user to be a logged in admin
  private final LocalUserServiceTestConfig localUserServiceTestConfig =
      new LocalUserServiceTestConfig().setOAuthUserId(CURR_USER_ID);
  private final LocalDatastoreServiceTestConfig localDatastoreServiceTestConfig =
      new LocalDatastoreServiceTestConfig();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(localUserServiceTestConfig, localDatastoreServiceTestConfig)
          .setEnvIsAdmin(true)
          .setEnvAuthDomain(AUTH_DOMAIN)
          .setEnvEmail(EMAIL)
          .setEnvIsLoggedIn(true)
          .setEnvAttributes(USER_ID_CONFIG);
  private DatastoreService localDatastore;
  private UserService localUserService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private SetGameStage setGameStage;
  private PlayerDatabase playerDatabase;
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String FORM_SUBMIT_PARAMETER = "setGameStageSubmit";
  private static final String CAREERPATH_PARAMETER = "careerPath";
  private static final String LEVEL_PARAMETER = "level";
  private static final String REDIRECTION_URL = "admin/SetGameStage.html";
  private static final String PROJECT_MANAGER = "Project Manager";
  private static final String WEB_DEVELOPER = "Web Developer";
  private static final String SOFTWARE_ENGINEERING = "Software Engineering";
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String LEVEL_1 = "1";
  private static final String LEVEL_2 = "2";
  private static final String NAME = "Bob";
  private static final String EMAIL = "Bob@email.com";
  private static final Gson gson = new Gson();

  @Before
  public void setUp() throws LoggedOutException {
    // initialize local user + datastore service. Current user is admin.
    helper.setUp();
    this.localUserService = UserServiceFactory.getUserService();
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.playerDatabase = new PlayerDatabase(this.localDatastore, this.localUserService);
    this.setGameStage = new SetGameStage();
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests that an admin can update their game state once */
  @Test
  public void doPost_AdminInputChangesTheirGameState() throws IOException, LoggedOutException {
    // set user as an arbitrary player
    User currentUser = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String currentGameStageId = PROJECT_MANAGER + LEVEL_2;
    createCurrentPlayerAndAddToDatabase(
        playerDatabase, currentUser, displayName, currentGameStageId);

    // mocks user entering in game stage data
    String careerPathInput = WEB_DEVELOPER;
    String levelInput = LEVEL_1;

    mockUserGameStageInput(careerPathInput, levelInput);

    this.setGameStage.doPost(this.request, this.response);

    // check that resulting user game stage is the input entered into form
    String expected = WEB_DEVELOPER + LEVEL_1;
    String result = this.playerDatabase.getEntityCurrentPageID();

    Assert.assertEquals(expected, result);
  }

  /** Tests that an admin can update their game state multiple times */
  @Test
  public void doPost_AdminInputChangesTheirGameState_MultipleTimes()
      throws IOException, LoggedOutException {
    // set user as an arbitrary player
    User currentUser = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String currentGameStageId = SOFTWARE_ENGINEERING + LEVEL_1;
    createCurrentPlayerAndAddToDatabase(
        playerDatabase, currentUser, displayName, currentGameStageId);

    // mocks user entering in game stage data
    String careerPathInput1 = WEB_DEVELOPER;
    String levelInput1 = LEVEL_2;

    mockUserGameStageInput(careerPathInput1, levelInput1);

    this.setGameStage.doPost(this.request, this.response);

    // check that resulting user game stage is the input entered into form
    String expected1 = WEB_DEVELOPER + LEVEL_2;
    String result1 = this.playerDatabase.getEntityCurrentPageID();

    Assert.assertEquals(expected1, result1);

    // admin changes game state a second time
    String careerPathInput2 = PROJECT_MANAGER;
    String levelInput2 = LEVEL_1;

    mockUserGameStageInput(careerPathInput2, levelInput2);

    this.setGameStage.doPost(this.request, this.response);

    // check that resulting user game stage is the input entered into form
    String expected2 = PROJECT_MANAGER + LEVEL_1;
    String result2 = this.playerDatabase.getEntityCurrentPageID();

    Assert.assertEquals(expected2, result2);
  }

  /** Test that logged out player causes LoggedOutException message to be written */
  @Test
  public void setGameStageWithLoggedOutUser_ExceptionMessageWritten() throws IOException {
    helper.setEnvIsLoggedIn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    // mocks user entering in game stage data
    String careerPathInput = WEB_DEVELOPER;
    String levelInput = LEVEL_2;

    mockUserGameStageInput(careerPathInput, levelInput);

    this.setGameStage.doPost(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(LOGGED_OUT_EXCEPTION));
  }

  private void createCurrentPlayerAndAddToDatabase(
      PlayerDatabase playerDatabase,
      User currentUser,
      String displayName,
      String currentGameStageId) {
    Player player = new Player(displayName, currentUser.getEmail(), "blah");
    player.setID(currentUser.getUserId());
    player.setCurrentPageID(currentGameStageId);
    this.playerDatabase.addPlayerToDatabase(player);
  }

  private void mockUserGameStageInput(String careerPathInput, String levelInput) {
    when(this.request.getParameter(FORM_SUBMIT_PARAMETER)).thenReturn(FORM_SUBMIT_PARAMETER);
    when(this.request.getParameter(CAREERPATH_PARAMETER)).thenReturn(careerPathInput);
    when(this.request.getParameter(LEVEL_PARAMETER)).thenReturn(levelInput);
  }
}
