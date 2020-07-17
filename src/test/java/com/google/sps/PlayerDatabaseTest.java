package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the PlayerDatabase to ensure that it puts, updates, and returns players correctly */
@RunWith(JUnit4.class)
public final class PlayerDatabaseTest {
  // Entity query strings from PlayerDatabase
  private static final String ENTITY_QUERY_STRING = "player";
  private static final String DISPLAY_NAME_QUERY_STRING = "displayName";
  private static final String EMAIL_QUERY_STRING = "email";
  private static final String ID_QUERY_STRING = "id";
  private static final String IMAGE_ID_QUERY_STRING = "imageID";
  private static final String CURRENT_PAGE_ID_QUERY_STRING = "currentPageID";
  // Mock current user for testing
  private static final String NAME = "Bob";
  private static final String NEW_NAME = "Bob2.0";
  private static final String EMAIL = "Bob@email.com";
  private static final String AUTH_DOMAIN = "email.com";
  private static final String CURR_USER_ID = "testid";
  private static final String IMAGE_ID = "imageId";
  private static final String NEW_IMAGE_ID = "newImageId";
  private static final String GAME_STAGE_ID = "gameStageId";
  private static final String NEW_GAME_STAGE_ID = "newGameStageId";
  private static Map<String, Object> USER_ID_CONFIG = new HashMap<>();

  static {
    USER_ID_CONFIG.put("com.google.appengine.api.users.UserService.user_id_key", CURR_USER_ID);
  }

  // creating a test user service, setting current user to be the above mock
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
  private PlayerDatabase playerDatabase;
  private static final Gson gson = new Gson();

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    UserService localUserService = UserServiceFactory.getUserService();
    this.playerDatabase = new PlayerDatabase(localDatastore, localUserService);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests that players are put into the database without raising exceptions */
  @Test
  public void storePlayer_worksSuccessfully() {
    Player player = new Player(NAME, EMAIL, CURR_USER_ID, IMAGE_ID, GAME_STAGE_ID);
    this.playerDatabase.addPlayerToDatabase(player);
  }

  /** Tests that the player entity is queried back sucessfully */
  @Test
  public void getPlayer_worksSuccessfully() throws Exception {
    Player player = new Player(NAME, EMAIL, CURR_USER_ID, IMAGE_ID, GAME_STAGE_ID);
    this.playerDatabase.addPlayerToDatabase(player);

    Entity expectedPlayerEntity = new Entity(ENTITY_QUERY_STRING);
    expectedPlayerEntity.setProperty(DISPLAY_NAME_QUERY_STRING, NAME);
    expectedPlayerEntity.setProperty(EMAIL_QUERY_STRING, EMAIL);
    expectedPlayerEntity.setProperty(ID_QUERY_STRING, CURR_USER_ID);
    expectedPlayerEntity.setProperty(IMAGE_ID_QUERY_STRING, IMAGE_ID);
    expectedPlayerEntity.setProperty(CURRENT_PAGE_ID_QUERY_STRING, GAME_STAGE_ID);

    Entity resultPlayerEntity = this.playerDatabase.getCurrentPlayerEntity();
    // convert to JsonObject to compare properties of elements

    JsonParser parser = new JsonParser();
    JsonObject resultJsonObject = parser.parse(gson.toJson(resultPlayerEntity)).getAsJsonObject();
    JsonObject expectedJsonObject =
        parser.parse(gson.toJson(expectedPlayerEntity)).getAsJsonObject();

    String result = resultJsonObject.get("propertyMap").toString();
    String expected = expectedJsonObject.get("propertyMap").toString();
    Assert.assertEquals(result, expected);
  }

  /** Test that all get methods return correct property */
  @Test
  public void getIndividualProperties_worksSuccessfully() throws Exception {
    Player player = new Player(NAME, EMAIL, CURR_USER_ID, IMAGE_ID, GAME_STAGE_ID);
    this.playerDatabase.addPlayerToDatabase(player);

    String expectedCurrentPageId = GAME_STAGE_ID;
    String resultCurrentPageId = this.playerDatabase.getEntityCurrentPageID();
    Assert.assertEquals(expectedCurrentPageId, resultCurrentPageId);

    String expectedImageId = IMAGE_ID;
    String resultImageId = this.playerDatabase.getEntityImageID();
    Assert.assertEquals(expectedImageId, resultImageId);

    String expectedId = CURR_USER_ID;
    String resultId = this.playerDatabase.getEntityID();
    Assert.assertEquals(expectedId, resultId);

    String expectedDisplayName = NAME;
    String resultDisplayName = this.playerDatabase.getEntityDisplayName();
    Assert.assertEquals(expectedDisplayName, resultDisplayName);
  }

  /** Test that setting the currentPageId updates it while keeping other properties constant */
  @Test
  public void setCurrentPageId_Succesfully() throws Exception {
    Player player = new Player(NAME, EMAIL, CURR_USER_ID, IMAGE_ID, GAME_STAGE_ID);
    this.playerDatabase.addPlayerToDatabase(player);

    this.playerDatabase.setEntityCurrentPageID(NEW_GAME_STAGE_ID);

    // check that current page id is set to correct value
    String expectedCurrentPageID = NEW_GAME_STAGE_ID;
    String resultCurrentPageID = this.playerDatabase.getEntityCurrentPageID();
    Assert.assertEquals(expectedCurrentPageID, resultCurrentPageID);

    // check that nothing else has changed
    String expectedImageId = IMAGE_ID;
    String resultImageId = this.playerDatabase.getEntityImageID();
    Assert.assertEquals(expectedImageId, resultImageId);

    String expectedId = CURR_USER_ID;
    String resultId = this.playerDatabase.getEntityID();
    Assert.assertEquals(expectedId, resultId);

    String expectedDisplayName = NAME;
    String resultDisplayName = this.playerDatabase.getEntityDisplayName();
    Assert.assertEquals(expectedDisplayName, resultDisplayName);
  }

  /** Set all properties to new properties, check that setting has worked */
  @Test
  public void setAllProperties() throws Exception {
    Player player = new Player(NAME, EMAIL, CURR_USER_ID, IMAGE_ID, GAME_STAGE_ID);
    this.playerDatabase.addPlayerToDatabase(player);

    this.playerDatabase.setEntityCurrentPageID(NEW_GAME_STAGE_ID);
    this.playerDatabase.setEntityImageID(NEW_IMAGE_ID);
    this.playerDatabase.setEntityDisplayName(NEW_NAME);

    Entity expectedPlayerEntity = new Entity(ENTITY_QUERY_STRING);
    expectedPlayerEntity.setProperty(DISPLAY_NAME_QUERY_STRING, NEW_NAME);
    expectedPlayerEntity.setProperty(EMAIL_QUERY_STRING, EMAIL);
    expectedPlayerEntity.setProperty(ID_QUERY_STRING, CURR_USER_ID);
    expectedPlayerEntity.setProperty(IMAGE_ID_QUERY_STRING, NEW_IMAGE_ID);
    expectedPlayerEntity.setProperty(CURRENT_PAGE_ID_QUERY_STRING, NEW_GAME_STAGE_ID);

    Entity resultPlayerEntity = this.playerDatabase.getCurrentPlayerEntity();
    // convert to JsonObject to compare properties of elements

    JsonParser parser = new JsonParser();
    JsonObject resultJsonObject = parser.parse(gson.toJson(resultPlayerEntity)).getAsJsonObject();
    JsonObject expectedJsonObject =
        parser.parse(gson.toJson(expectedPlayerEntity)).getAsJsonObject();

    String result = resultJsonObject.get("propertyMap").toString();
    String expected = expectedJsonObject.get("propertyMap").toString();
    Assert.assertEquals(result, expected);
  }
}
