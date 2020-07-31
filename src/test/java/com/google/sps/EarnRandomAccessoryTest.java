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
import com.google.sps.data.Accessory;
import com.google.sps.data.Accessory.Type;
import com.google.sps.data.AccessoryDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.EarnRandomAccessory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

/** Tests the EarnRandomAccessory servlet */
@RunWith(JUnit4.class)
public final class EarnRandomAccessoryTest {
  // RNG seed constant
  private static final int RANDOM_NUMBER_SEED = 24;
  // GameStage and Accessory hard coded constants
  private static final String GAME_STAGE_ID = "gamestage";
  private static final int NO_EXPERIENCE = 0;
  private static final int THRESHOLD = 15;
  private static final Type SAMPLE_TYPE = Type.HAT;
  private static final String ACCESSORIES_FOLDER = "accessories";
  private static final String FILEPATH = "filepath1.png";
  private static final int XPOS = 50;
  private static final int YPOS = 100;
  private static final int HEIGHT = 75;
  private static final int WIDTH = 150;
  // sample accessory ids
  private static final String COWBOY_HAT = "cowboy";
  private static final String PARTY_HAT = "partyhat";
  private static final String SUNGLASSES = "sunglasses";
  private static final String NERD_GLASSES = "nerdGlasses";
  private static final String DOG = "dog";
  private static final String CAT = "cat";

  // exception messages
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String ENTITY_NOT_FOUND_EXCEPTION =
      "Earned id cannot be found in the database.";
  private static final String NO_ACCESSORIES = "\"none\"";

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
  private EarnRandomAccessory earnRandomAccessory;
  private DatastoreService localDatastore;
  private UserService localUserService;

  private static final Gson gson = new Gson();
  private Random randomGenerator;

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    MockitoAnnotations.initMocks(this);
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.localUserService = UserServiceFactory.getUserService();
    this.accessoryDatabase = new AccessoryDatabase(localDatastore);
    this.playerDatabase = new PlayerDatabase(localDatastore, localUserService);
    this.randomGenerator = new Random();
    this.randomGenerator.setSeed(RANDOM_NUMBER_SEED);
    this.earnRandomAccessory = new EarnRandomAccessory();
    this.earnRandomAccessory.init();
    this.earnRandomAccessory.setRandomNumberGenerator(this.randomGenerator);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** doGet method should respond with Json of a random accessory that the player does not have */
  @Test
  public void doGet_whenCalledOnce_respondsWithRandomAccessoryThatPlayerDoesNotHave()
      throws IOException, LoggedOutException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    // add user and all their available accessories
    User user = this.localUserService.getCurrentUser();
    List<String> initialPlayerAccessoryIds = Arrays.asList(SUNGLASSES, DOG, COWBOY_HAT);
    createCurrentPlayerAndAddToDatabase(user, initialPlayerAccessoryIds);

    List<String> allAccessoryIds =
        Arrays.asList(COWBOY_HAT, PARTY_HAT, SUNGLASSES, NERD_GLASSES, DOG, CAT);
    List<Accessory> allAccessories = new ArrayList();
    for (String accessoryId : allAccessoryIds) {
      allAccessories.add(createAccessoryAndAddToDatabase(accessoryId));
    }

    this.earnRandomAccessory.doGet(this.request, this.response);
    Accessory earnedAccessory = gson.fromJson(stringWriter.toString(), Accessory.class);
    List<String> resultPlayerAccessoryIds = this.playerDatabase.getEntityAllAccessoryIDs();

    // checks that earnRandomAccessory responds with a Json string containing accessory object
    // that is in all accessories but not in the initial player accessories, and is now in
    // the resultant player accessories
    Assert.assertTrue(allAccessoryIds.contains(earnedAccessory.getId()));
    Assert.assertTrue(!initialPlayerAccessoryIds.contains(earnedAccessory.getId()));
    Assert.assertTrue(resultPlayerAccessoryIds.contains(earnedAccessory.getId()));
  }

  /** If no accessories are left, a string with a no accessories message is returned */
  @Test
  public void doGet_withNoUnearnedAccessoriesLeft_respondsWithNone()
      throws IOException, LoggedOutException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    // add user and all their available accessories
    User user = this.localUserService.getCurrentUser();
    List<String> initialPlayerAccessoryIds =
        Arrays.asList(COWBOY_HAT, PARTY_HAT, SUNGLASSES, NERD_GLASSES, DOG, CAT);
    createCurrentPlayerAndAddToDatabase(user, initialPlayerAccessoryIds);

    List<String> allAccessoryIds =
        Arrays.asList(COWBOY_HAT, PARTY_HAT, SUNGLASSES, NERD_GLASSES, DOG, CAT);
    List<Accessory> allAccessories = new ArrayList();
    for (String accessoryId : allAccessoryIds) {
      allAccessories.add(createAccessoryAndAddToDatabase(accessoryId));
    }

    this.earnRandomAccessory.doGet(this.request, this.response);

    // checks that earnRandomAccessory responds with a Json string containing "none"
    String expected = NO_ACCESSORIES;
    String result = stringWriter.toString().trim();
    Assert.assertEquals(expected, result);
  }

  /** Run multiple times. All available accessories are earned until no more are left */
  @Test
  public void doGet_CalledMultipleTimes_keepsRespondingWithAccessoriesUntilNoMoreAreLeft()
      throws IOException, LoggedOutException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    // add user and all their available accessories
    User user = this.localUserService.getCurrentUser();
    List<String> initialPlayerAccessoryIds =
        new ArrayList<String>(Arrays.asList(SUNGLASSES, DOG, COWBOY_HAT));
    createCurrentPlayerAndAddToDatabase(user, initialPlayerAccessoryIds);

    List<String> allAccessoryIds =
        Arrays.asList(COWBOY_HAT, PARTY_HAT, SUNGLASSES, NERD_GLASSES, DOG, CAT);
    List<Accessory> allAccessories = new ArrayList();
    for (String accessoryId : allAccessoryIds) {
      allAccessories.add(createAccessoryAndAddToDatabase(accessoryId));
    }
    // doGet 3 times to earn the last 3 accessories
    for (int i = 0; i < 3; i++) {
      // clearing string writer
      printWriter.flush();
      stringWriter.getBuffer().setLength(0);

      this.earnRandomAccessory.doGet(this.request, this.response);
      Accessory earnedAccessory = gson.fromJson(stringWriter.toString(), Accessory.class);
      List<String> resultPlayerAccessoryIds = this.playerDatabase.getEntityAllAccessoryIDs();

      // checks that earnRandomAccessory responds with a Json string containing accessory object
      // that is in all accessories but not in the initial player accessories, and is now in
      // the resultant player accessories
      Assert.assertTrue(allAccessoryIds.contains(earnedAccessory.getId()));
      Assert.assertTrue(!initialPlayerAccessoryIds.contains(earnedAccessory.getId()));
      Assert.assertTrue(resultPlayerAccessoryIds.contains(earnedAccessory.getId()));

      initialPlayerAccessoryIds.add(earnedAccessory.getId());
    }
    // clearing string writer
    printWriter.flush();
    stringWriter.getBuffer().setLength(0);

    // now that all accessories earned,
    // checks that earnRandomAccessory responds with a Json string containing "none"
    this.earnRandomAccessory.doGet(this.request, this.response);

    String expected = NO_ACCESSORIES;
    String result = stringWriter.toString().trim();
    Assert.assertEquals(expected, result);
  }

  /** Test that logged out player causes LoggedOutException message to be written */
  @Test
  public void doGet_withLoggedOutUser_ExceptionMessageWritten() throws IOException {
    helper.setEnvIsLoggedIn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    this.earnRandomAccessory.doGet(this.request, this.response);

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
