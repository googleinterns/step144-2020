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
import com.google.sps.data.Accessory;
import com.google.sps.data.Accessory.Type;
import com.google.sps.data.AccessoryDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.GetPlayerAccessories;
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

/** Tests the GetPlayerAccessories servlet */
@RunWith(JUnit4.class)
public final class GetPlayerAccessoriesTest {
  // GameStage and Accessory hard coded constants
  private static final String GAME_STAGE_ID = "gamestage";
  private static final Type SAMPLE_TYPE = Type.HAT;
  private static final String HAT_ID = "hat";
  private static final String GLASSES_ID = "glasses";
  private static final String COMPANION_ID = "companion";
  private static final List<String> ALL_ACCESSORIES =
      Arrays.asList(HAT_ID, GLASSES_ID, COMPANION_ID);
  private static final String FILEPATH = "filepath";
  private static final int HEIGHT = 30;
  private static final int WIDTH = 20;
  private static final int XPOS = 40;
  private static final int YPOS = 5;
  private static final int NO_EXPERIENCE = 0;
  private static final int THRESHOLD = 15;

  // exception messages
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String NOT_FOUND_EXCEPTION =
      "Something went wrong. The accessory was not found in the database.";

  // creating a test user service, setting current user to be a logged in admin
  private static final String NAME = "trustme";
  private static final String EMAIL = "trustme@notspam.com";
  private static final String AUTH_DOMAIN = "notspam.com";
  private static final String CURRENT_USER_ID = "woot";
  private static final String IMAGE_ID = "shadoop";
  private static final String USER_ID_KEY_PATH =
      "com.google.appengine.api.users.UserService.user_id_key";
  private static Map<String, Object> USER_ID_CONFIG =
      new HashMap<String, Object>() {
        {
          put(USER_ID_KEY_PATH, CURRENT_USER_ID);
        }
      };
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

  // mocks and databases
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private AccessoryDatabase accessoryDatabase;
  private PlayerDatabase playerDatabase;
  private GetPlayerAccessories getPlayerAccessories;
  private DatastoreService localDatastore;
  private UserService localUserService;

  private static final Gson gson = new Gson();

  @Before
  public void setUp() throws LoggedOutException {
    helper.setUp(); // initialize local datastore for testing
    MockitoAnnotations.initMocks(this);
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.localUserService = UserServiceFactory.getUserService();
    this.accessoryDatabase = new AccessoryDatabase(localDatastore);
    this.playerDatabase = new PlayerDatabase(localDatastore, localUserService);
    this.getPlayerAccessories = new GetPlayerAccessories();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** doGet method should respond with Json message with all accessories available to the player */
  @Test
  public void testThatDoGetRespondsWithAllPlayerAccessories() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    // add user and all their available accessories
    User user = this.localUserService.getCurrentUser();
    List<String> playerAccessoryIDs = Arrays.asList(HAT_ID, GLASSES_ID, COMPANION_ID);
    createCurrentPlayerAndAddToDatabase(user, playerAccessoryIDs);

    String accessory1Id = HAT_ID;
    String accessory2Id = GLASSES_ID;
    String accessory3Id = COMPANION_ID;
    Accessory accessory1 = createAccessoryAndAddToDatabase(accessory1Id);
    Accessory accessory2 = createAccessoryAndAddToDatabase(accessory2Id);
    Accessory accessory3 = createAccessoryAndAddToDatabase(accessory3Id);

    String expectedJson = gson.toJson(Arrays.asList(accessory1, accessory2, accessory3));

    this.getPlayerAccessories.doGet(this.request, this.response);

    // checks that getPlayerAccessories responds with a Json string containing each accessory
    // object
    JsonElement expected = JsonParser.parseString(expectedJson);
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(expected, result);
  }

  /** when an accessory is not found, it is not included in response and gameplay continues */
  @Test
  public void testThatNotFoundAccessoriesAreNotIncludedInResponse() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    // add user and all their available accessories
    User user = this.localUserService.getCurrentUser();
    List<String> playerAccessoryIDs = Arrays.asList(HAT_ID, GLASSES_ID, COMPANION_ID);
    createCurrentPlayerAndAddToDatabase(user, playerAccessoryIDs);

    // the companion id is not added to database, so a EntityNotFoundException will be thrown
    String accessory1Id = HAT_ID;
    String accessory2Id = GLASSES_ID;
    Accessory accessory1 = createAccessoryAndAddToDatabase(accessory1Id);
    Accessory accessory2 = createAccessoryAndAddToDatabase(accessory2Id);

    String expectedJson = gson.toJson(Arrays.asList(accessory1, accessory2));

    this.getPlayerAccessories.doGet(this.request, this.response);

    // checks that getPlayerAccessories responds with a Json string containing each accessory
    // object, but without the accessory not in the database
    JsonElement expected = JsonParser.parseString(expectedJson);
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(expected, result);
  }

  /** Test that logged out player causes LoggedOutException message to be written */
  @Test
  public void getPlayerAccessoriesWithLoggedOutUser_ExceptionMessageWritten() throws IOException {
    helper.setEnvIsLoggedIn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    this.getPlayerAccessories.doGet(this.request, this.response);

    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(LOGGED_OUT_EXCEPTION));
  }

  /** Create a player with variable accessories. Other attributes are hardcoded. */
  private void createCurrentPlayerAndAddToDatabase(User currentUser, List<String> accessoryIDs) {
    Player player =
        new Player(
            NAME,
            currentUser.getEmail(),
            currentUser.getUserId(),
            IMAGE_ID,
            GAME_STAGE_ID,
            accessoryIDs,
            NO_EXPERIENCE,
            THRESHOLD);
    this.playerDatabase.addPlayerToDatabase(player);
  }

  /** Create an accessory with variable ID. Other attributes are hardcoded. */
  private Accessory createAccessoryAndAddToDatabase(String id) {
    Accessory accessory = new Accessory(id, FILEPATH, SAMPLE_TYPE, HEIGHT, WIDTH, XPOS, YPOS);
    this.accessoryDatabase.storeAccessory(accessory);
    return accessory;
  }
}
