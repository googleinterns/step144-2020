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
import com.google.sps.servlets.ExperienceServlet;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// Responsible for testing the ExperienceServlet to make sure that the image
// being properly served.
@RunWith(JUnit4.class)
public final class ExperienceServletTest {
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
  // creating a test user service
  private final LocalUserServiceTestConfig localUserServiceTestConfig =
      new LocalUserServiceTestConfig().setOAuthUserId(CURR_USER_ID);
  private final LocalDatastoreServiceTestConfig localDatastoreServiceTestConfig =
      new LocalDatastoreServiceTestConfig();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(localUserServiceTestConfig, localDatastoreServiceTestConfig)
          .setEnvIsAdmin(false)
          .setEnvAuthDomain(AUTH_DOMAIN)
          .setEnvEmail(EMAIL)
          .setEnvIsLoggedIn(true)
          .setEnvAttributes(USER_ID_CONFIG);
  private DatastoreService localDatastore;
  private UserService localUserService;
  private PlayerDatabase playerDatabase;
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String DISPLAY_NAME = "TestName";
  private static final String EMAIL = "TestName@email.com";
  private static final Gson gson = new Gson();
  private static final String EXPERIENCE_PARAMETER = "experience";
  private static final String TEST_EXPERIENCE = "12";
  private static final String TEST_EXPERIENCE_PLUS_ONE = "13";
  private static final String NEW_LINE = "\n";
  private int experience;
  private static ExperienceServlet experienceServlet;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before
  public void setUp() {
    helper.setUp();
    this.localUserService = UserServiceFactory.getUserService();
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.experienceServlet = new ExperienceServlet();
    this.experienceServlet.init();
    this.experience = 12;
    this.playerDatabase = new PlayerDatabase(this.localDatastore, this.localUserService);
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Rule public ExpectedException notImplementedRule = ExpectedException.none();

  @Test
  public void doGet_successfulPath_retrievePlayerExp() throws IOException, LoggedOutException {
    notImplementedRule.expect(UnsupportedOperationException.class);
    User currentUser = this.localUserService.getCurrentUser();
    Player player = createCurrentPlayer(this.playerDatabase, currentUser);
    this.playerDatabase.addPlayerToDatabase(player);
    this.playerDatabase.setEntityExperience(12);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.experienceServlet.doGet(this.request, this.response);
    String result = stringWriter.toString().split(NEW_LINE)[0];
    Assert.assertTrue(result.equals(TEST_EXPERIENCE));
  }

  @Test
  public void doPost_successfulPath_updatesPlayerExp() throws IOException, LoggedOutException {
    notImplementedRule.expect(UnsupportedOperationException.class);
    User currentUser = this.localUserService.getCurrentUser();
    Player player = createCurrentPlayer(this.playerDatabase, currentUser);
    this.playerDatabase.addPlayerToDatabase(player);
    when(this.request.getParameter(EXPERIENCE_PARAMETER)).thenReturn(TEST_EXPERIENCE_PLUS_ONE);
    this.experienceServlet.doPost(this.request, this.response);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.experienceServlet.doGet(this.request, this.response);
    String result = stringWriter.toString().split(NEW_LINE)[0];
    Assert.assertTrue(result.equals(TEST_EXPERIENCE_PLUS_ONE));
  }

  private Player createCurrentPlayer(PlayerDatabase playerDatabase, User currentUser) {
    Player player = new Player(DISPLAY_NAME, currentUser.getEmail(), CURR_USER_ID);
    player.setID(currentUser.getUserId());
    return player;
  }
}
