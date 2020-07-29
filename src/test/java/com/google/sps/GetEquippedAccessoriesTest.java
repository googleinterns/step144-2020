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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sps.data.Accessory;
import com.google.sps.data.Accessory.Type;
import com.google.sps.data.AccessoryDatabase;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.GetEquippedAccessories;
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
public final class GetEquippedAccessoriesTest {
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
  private AccessoryDatabase accessoryDatabase;
  private PlayerDatabase playerDatabase;
  private GetEquippedAccessories getEquippedAccessories;
  private DatastoreService localDatastore;
  private UserService localUserService;
  private static final Gson gson = new Gson();
  private static final JsonParser jsonParser = new JsonParser();
  private static final String GAME_STAGE_ID = "gamestage";
  private static final String EQUIPPED_HAT = "equippedHat";
  private static final String EQUIPPED_GLASSES = "equippedGlasses";
  private static final String EQUIPPED_COMPANION = "equippedCompanion";
  private static final String NONE_EQUIPPED_JSON = "\"noneEquipped\"";
  private static final String COWBOY_HAT_ID = "cowboyhat";
  private static final String SUNGLASSES_ID = "sunglasses";
  private static final String BOXY_GLASSES_ID = "boxyglasses";
  private static final String DOG_ID = "dog";
  private static final String CAT_ID = "cat";
  private static final String FILEPATH = "filepath";
  private static final int HEIGHT = 30;
  private static final int WIDTH = 20;
  private static final int XPOS = 40;
  private static final int YPOS = 5;
  private static final int NO_EXPERIENCE = 0;
  private static final int THRESHOLD = 15;
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String NOT_FOUND_EXCEPTION =
      "Something went wrong. The accessory was not found in the database.";

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    MockitoAnnotations.initMocks(this);
    this.localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.localUserService = UserServiceFactory.getUserService();
    this.accessoryDatabase = new AccessoryDatabase(localDatastore);
    this.playerDatabase = new PlayerDatabase(localDatastore, localUserService);
    this.getEquippedAccessories = new GetEquippedAccessories();
    this.getEquippedAccessories.init();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_RespondsWithEquippedAccessories() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    User user = this.localUserService.getCurrentUser();
    List<String> accessories = Arrays.asList(COWBOY_HAT_ID, BOXY_GLASSES_ID, CAT_ID);
    String equippedHatID = COWBOY_HAT_ID;
    String equippedGlassesID = BOXY_GLASSES_ID;
    String equippedCompanionID = CAT_ID;
    createCurrentPlayerAndAddToDatabase(
        user, accessories, equippedHatID, equippedGlassesID, equippedCompanionID);
    Accessory hat = createAccessoryAndAddToDatabase(equippedHatID, Type.HAT);
    Accessory glasses = createAccessoryAndAddToDatabase(equippedGlassesID, Type.GLASSES);
    Accessory companion = createAccessoryAndAddToDatabase(equippedCompanionID, Type.COMPANION);

    JsonObject expectedJsonObject = new JsonObject();
    expectedJsonObject.add(EQUIPPED_HAT, gson.toJsonTree(hat));
    expectedJsonObject.add(EQUIPPED_GLASSES, gson.toJsonTree(glasses));
    expectedJsonObject.add(EQUIPPED_COMPANION, gson.toJsonTree(companion));

    this.getEquippedAccessories.doGet(this.request, this.response);

    // checks that getEquippedAccessories responds with a Json string containing each accessory
    // object
    String expected = expectedJsonObject.toString();
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(expected));
  }

  /** "notEquipped" is returned if player does not have an id to an accessory */
  @Test
  public void doGet_RespondsNotEquippedIfQueriedAccessoryIsNotEquipped() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    User user = this.localUserService.getCurrentUser();
    List<String> accessories = Arrays.asList(COWBOY_HAT_ID, BOXY_GLASSES_ID, CAT_ID);
    String equippedHatID = null;
    String equippedGlassesID = SUNGLASSES_ID;
    String equippedCompanionID = DOG_ID;
    createCurrentPlayerAndAddToDatabase(
        user, accessories, equippedHatID, equippedGlassesID, equippedCompanionID);
    Accessory glasses = createAccessoryAndAddToDatabase(equippedGlassesID, Type.GLASSES);
    Accessory companion = createAccessoryAndAddToDatabase(equippedCompanionID, Type.COMPANION);

    JsonObject expectedJsonObject = new JsonObject();
    expectedJsonObject.add(EQUIPPED_HAT, jsonParser.parse(NONE_EQUIPPED_JSON));
    expectedJsonObject.add(EQUIPPED_GLASSES, gson.toJsonTree(glasses));
    expectedJsonObject.add(EQUIPPED_COMPANION, gson.toJsonTree(companion));

    this.getEquippedAccessories.doGet(this.request, this.response);

    // checks that getEquippedAccessories responds with a Json string containing noneEquipped
    // for the hat
    String expected = expectedJsonObject.toString();
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(expected));
  }

  /** "notEquipped" is returned if EntityNotFound is thrown when looking for accessory */
  @Test
  public void doGet_RespondsNotEquippedIfQueriedAccessoryIsNotFound() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    User user = this.localUserService.getCurrentUser();
    List<String> accessories = Arrays.asList(COWBOY_HAT_ID, SUNGLASSES_ID, CAT_ID);
    String equippedHatID = COWBOY_HAT_ID;
    String equippedGlassesID = SUNGLASSES_ID;
    String equippedCompanionID = CAT_ID;
    createCurrentPlayerAndAddToDatabase(
        user, accessories, equippedHatID, equippedGlassesID, equippedCompanionID);
    Accessory hat = createAccessoryAndAddToDatabase(equippedHatID, Type.HAT);
    Accessory companion = createAccessoryAndAddToDatabase(equippedCompanionID, Type.COMPANION);

    JsonObject expectedJsonObject = new JsonObject();
    expectedJsonObject.add(EQUIPPED_HAT, gson.toJsonTree(hat));
    expectedJsonObject.add(EQUIPPED_GLASSES, jsonParser.parse(NONE_EQUIPPED_JSON));
    expectedJsonObject.add(EQUIPPED_COMPANION, gson.toJsonTree(companion));

    this.getEquippedAccessories.doGet(this.request, this.response);

    // checks that getEquippedAccessories responds with a Json string containing noneEquipped
    // for the glasses
    String expected = expectedJsonObject.toString();
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(expected));
  }

  /** Test that logged out player causes LoggedOutException message to be written */
  @Test
  public void getEquippedAccessoriesWithLoggedOutUser_ExceptionMessageWritten() throws IOException {
    helper.setEnvIsLoggedIn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);

    this.getEquippedAccessories.doGet(this.request, this.response);

    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(LOGGED_OUT_EXCEPTION));
  }

  /** Create a player with variable accessories. Other attributes are hardcoded. */
  private void createCurrentPlayerAndAddToDatabase(
      User currentUser,
      List<String> allAccessoryIDs,
      String equippedHatID,
      String equippedGlassesID,
      String equippedCompanionID) {
    Player player =
        new Player(
            NAME,
            currentUser.getEmail(),
            currentUser.getUserId(),
            IMAGE_ID,
            GAME_STAGE_ID,
            allAccessoryIDs,
            NO_EXPERIENCE,
            THRESHOLD);
    player.setEquippedHatID(equippedHatID);
    player.setEquippedGlassesID(equippedGlassesID);
    player.setEquippedCompanionID(equippedCompanionID);
    this.playerDatabase.addPlayerToDatabase(player);
  }

  /** Create an accessory with variable ID and type. Other attributes are hardcoded. */
  private Accessory createAccessoryAndAddToDatabase(String id, Type type) {
    Accessory accessory = new Accessory(id, FILEPATH, type, HEIGHT, WIDTH, XPOS, YPOS);
    this.accessoryDatabase.storeAccessory(accessory);
    return accessory;
  }
}
