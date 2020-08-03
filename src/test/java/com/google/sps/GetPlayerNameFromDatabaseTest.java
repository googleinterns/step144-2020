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
import com.google.sps.servlets.GetPlayerNameFromDatabase;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
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

// Responsible for testing the GetPlayerNameFromDatabase to make sure that the dialogue is
// being parsed correctly
@RunWith(JUnit4.class)
public final class GetPlayerNameFromDatabaseTest {
  private static final String CURRENT_PAGE_ID = "test1";
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
  // creating a test user service to mock the doget method
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
  private static final String DISPLAY_NAME = "admin";
  private static final String EMAIL = "admin@admin.com";
  private static final Gson gson = new Gson();
  private static GetPlayerNameFromDatabase getPlayerNameServlet;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Rule public ExpectedException canNotInitialize = ExpectedException.none();

  @Before
  public void setUp() throws ServletException {
    helper.setUp();
    this.localUserService = UserServiceFactory.getUserService();
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.playerDatabase = new PlayerDatabase(this.localDatastore, this.localUserService);
    this.getPlayerNameServlet = new GetPlayerNameFromDatabase();
    this.getPlayerNameServlet.init();
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_getPlayerNameFromDatabase() throws IOException, LoggedOutException {
    User currentUser = this.localUserService.getCurrentUser();
    Player player = createCurrentPlayer(this.playerDatabase, currentUser);
    this.playerDatabase.addPlayerToDatabase(player);
    this.playerDatabase.setEntityCurrentPageID(CURRENT_PAGE_ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    when(this.response.getWriter()).thenReturn(printWriter);
    this.getPlayerNameServlet.doGet(this.request, this.response);

    String result = stringWriter.toString();
    String expected = DISPLAY_NAME;
    Assert.assertEquals(expected, result);
  }

  @Test
  public void doGet_getPlayerNameFromDatabase_LoggedOutException()
      throws IOException, LoggedOutException {
    helper.setEnvIsLoggedIn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    when(this.response.getWriter()).thenReturn(printWriter);
    this.getPlayerNameServlet.doGet(this.request, this.response);

    String result = stringWriter.toString();
    Assert.assertEquals(result, LOGGED_OUT_EXCEPTION);
  }

  private Player createCurrentPlayer(PlayerDatabase playerDatabase, User currentUser) {
    Player player = new Player(DISPLAY_NAME, currentUser.getEmail(), CURR_USER_ID);
    player.setID(currentUser.getUserId());
    return player;
  }
}
