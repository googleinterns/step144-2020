package com.google.sps;

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
import com.google.sps.servlets.DeleteUserServlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

/** Tests that the DeleteUserServlet servlet correctly deletes a player from the database */
@RunWith(JUnit4.class)
public final class DeleteUserServletTest {
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
  private DeleteUserServlet deleteUserServlet;
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String DISPLAY_NAME = "TestName";
  private static final String EMAIL = "TestName@email.com";
  private static final Gson gson = new Gson();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before
  public void setUp() throws LoggedOutException {
    // initialize local user + datastore service.
    helper.setUp();
    this.localUserService = UserServiceFactory.getUserService();
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.deleteUserServlet = new DeleteUserServlet();
    this.playerDatabase = new PlayerDatabase(this.localDatastore, this.localUserService);
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Rule public ExpectedException userIsNotSavedExceptionRule = ExpectedException.none();

  /** Tests that the player is being deleted */
  @Test
  public void doPost_CurrentUserIsDeleted() throws IOException, LoggedOutException {
    User currentUser = this.localUserService.getCurrentUser();
    Player player = createCurrentPlayer(this.playerDatabase, currentUser);
    this.playerDatabase.addPlayerToDatabase(player);
    this.deleteUserServlet.doPost(this.request, this.response);

    List<Player> result = this.playerDatabase.getPlayers();
    Assert.assertEquals(result.contains(player), false);
  }

  private Player createCurrentPlayer(PlayerDatabase playerDatabase, User currentUser) {
    Player player = new Player(DISPLAY_NAME, currentUser.getEmail(), CURR_USER_ID);
    player.setID(currentUser.getUserId());
    return player;
  }

  /** Tests that multiple players can be deleted */
  @Test
  public void doPost_MultipleCurrentUsersAreDeleted() throws IOException, LoggedOutException {
    User currentUser = this.localUserService.getCurrentUser();
    Player player = createCurrentPlayer(this.playerDatabase, currentUser);
    this.playerDatabase.addPlayerToDatabase(player);
    this.playerDatabase.addPlayerToDatabase(player);
    this.playerDatabase.addPlayerToDatabase(player);
    this.deleteUserServlet.doPost(this.request, this.response);

    List<Player> result = this.playerDatabase.getPlayers();
    Assert.assertEquals(result.contains(player), false);
  }

  /** Tests that an unsaved player can not be deleted */
  @Test
  public void doPost_ErrorWhenDeletingUserThatIsNotSaved() throws IOException, LoggedOutException {
    User currentUser = this.localUserService.getCurrentUser();
    Player player = createCurrentPlayer(this.playerDatabase, currentUser);
    userIsNotSavedExceptionRule.expect(NullPointerException.class);
    this.deleteUserServlet.doPost(this.request, this.response);
  }
}
