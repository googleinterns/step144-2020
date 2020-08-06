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
import com.google.sps.servlets.InitializeGameStage;
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

/** Tests that the InitializeGameStage servlet correctly updates game stage to be an admin */
@RunWith(JUnit4.class)
public final class InitializeGameStageTest {
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
          .setEnvAuthDomain(AUTH_DOMAIN)
          .setEnvEmail(EMAIL)
          .setEnvIsLoggedIn(true)
          .setEnvAttributes(USER_ID_CONFIG);
  private DatastoreService localDatastore;
  private UserService localUserService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private InitializeGameStage initializeGameStage;
  private PlayerDatabase playerDatabase;
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String FORM_SUBMIT_PARAMETER = "pathSubmit";
  private static final String PROJECT_MANAGER = "Project Manager";
  private static final String WEB_DEVELOPER = "Web Developer";
  private static final String SOFTWARE_ENGINEER = "Software Engineer";
  private static final String DATA_SCIENTIST = "Data Scientist";
  private static final List<String> PATH_OPTIONS =
      Arrays.asList(PROJECT_MANAGER, WEB_DEVELOPER, SOFTWARE_ENGINEER, DATA_SCIENTIST);
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String LEVEL_1 = "1";
  private static final String IMAGE_ID = "image";
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
    this.initializeGameStage = new InitializeGameStage();
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests that player choosing path correctly sets game stage */
  @Test
  public void doPost_ButtonSetsInitialGameStage() throws IOException, LoggedOutException {
    // set user as an arbitrary player
    User currentUser = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String currentGameStageId = new String();
    createCurrentPlayerAndAddToDatabase(
        playerDatabase, currentUser, displayName, currentGameStageId);

    // mocks user clicking a career path button
    when(this.request.getParameter(FORM_SUBMIT_PARAMETER)).thenReturn(DATA_SCIENTIST);

    this.initializeGameStage.doPost(this.request, this.response);

    // check that resulting user game stage is the first level of the path selected
    String expected = DATA_SCIENTIST + LEVEL_1;
    String result = this.playerDatabase.getEntityCurrentPageID();
    Assert.assertEquals(expected, result);
  }

  /** Tests that all choices set player game stage to correct value */
  @Test
  public void doPost_AllButtonsSetInitialGameStage() throws IOException, LoggedOutException {
    // set user as an arbitrary player
    User currentUser = this.localUserService.getCurrentUser();
    String displayName = NAME;
    String currentGameStageId = new String();
    createCurrentPlayerAndAddToDatabase(
        playerDatabase, currentUser, displayName, currentGameStageId);

    // mocks user clicking each button button
    for (String careerPathChoice : PATH_OPTIONS) {
      when(this.request.getParameter(FORM_SUBMIT_PARAMETER)).thenReturn(careerPathChoice);

      this.initializeGameStage.doPost(this.request, this.response);

      // check that resulting user game stage is the first level of the path selected
      String expected = careerPathChoice + LEVEL_1;
      String result = this.playerDatabase.getEntityCurrentPageID();
      Assert.assertEquals(expected, result);
    }
  }

  /** Test that logged out player causes LoggedOutException message to be written */
  @Test
  public void initializeGameStageWithLoggedOutUser_ExceptionMessageWritten() throws IOException {
    helper.setEnvIsLoggedIn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    when(this.request.getParameter(FORM_SUBMIT_PARAMETER)).thenReturn(WEB_DEVELOPER);
    this.initializeGameStage.doPost(this.request, this.response);

    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(LOGGED_OUT_EXCEPTION));
  }

  private void createCurrentPlayerAndAddToDatabase(
      PlayerDatabase playerDatabase,
      User currentUser,
      String displayName,
      String currentGameStageId) {
    Player player = new Player(displayName, currentUser.getEmail(), IMAGE_ID);
    player.setID(currentUser.getUserId());
    player.setCurrentPageID(currentGameStageId);
    this.playerDatabase.addPlayerToDatabase(player);
  }
}
