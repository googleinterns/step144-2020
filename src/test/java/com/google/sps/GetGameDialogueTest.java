package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.GetGameDialogue;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public final class GetGameDialogueTest {
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private final LocalServiceTestHelper helper = 
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final Gson gson = new Gson();
  //variable for testing GameStage
  private static String QUERY_FOR_GAMESTAGE_ENTITY = "gamestage";
  private static final String SOFTWARE_ENGINEER_LEVEL1_NAME = "Software Engineer Level 1";
  private static final String SOFTWARE_ENGINEER_INPUT = "Software Engineer";
  private static final String WEBDEV_LEVEL1_NAME = "Web Developer Level 1";
  private static final String WEBDEV_INPUT = "Web Developer";
  private static final String LEVEL_1 = "1";
  private static final String LEVEL_2 = "2";
  private static final String TEST_CONTENT = "Some test content here.";
  private static final Boolean IS_NOT_LAST_STAGE = false;
  private static final Boolean IS_LAST_STAGE = true;
  private String NAME_PROPERTY = "name";
  private String ID_PROPERTY = "id";
  private String CONTENT_PROPERTY = "content";
  private String QUIZ_KEY_PROPERTY = "quizkey";
  private String IS_LAST_STAGE_PROPERTY = "isFinalStage";
  private String NEXT_STAGE_PROPERTY = "nextstage";
  private GetGameDialogue getGameDialogue = new GetGameDialogue();
  //player variables
  private static String NAME_PLAYER = "many_admin";
  private static String EMAIL_PLAYER = "admin@google.com";
  private static String EXEPTION_PLAYER = "there is no player inside the list";
  //database variables
  private GameStageDatabase gameStageDatabase;
  private PlayerDatabase playerDatabase;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp(); // initialize local datastore for testing
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.playerDatabase = new PlayerDatabase(localDatastore);
    this.gameStageDatabase = new GameStageDatabase(localDatastore);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests that the objects that are put into the database without raising exceptions */
  @Test
  public void storePlayerinPlayerDatabaseAndComparePageIDs_worksSuccessfully() throws Exception {

    String name1 = NAME_PLAYER;
    String email1 = EMAIL_PLAYER;
    String id1 = SOFTWARE_ENGINEER_INPUT + LEVEL_1;
    Player admin = new Player(name1, email1);
    admin.setCurrentPageID(id1);
    this.playerDatabase.addPlayerToDatabase(admin);
    try{
      List<Player> playerList = playerDatabase.getPlayers();
      Assert.assertEquals(playerList.get(0).getCurrentPageID(), admin.getCurrentPageID());
    } catch(Exception nullPointer){
      throw new Exception(EXEPTION_PLAYER);
    }
  }

  /** Tests that the objects that queried from the database sucessfully and compare the content by getting a gameStage*/
  @Test
  public void getGameStage_OutputsworksSuccessfully1() {
    String name = WEBDEV_LEVEL1_NAME;
    String content = TEST_CONTENT;
    String id = WEBDEV_INPUT + LEVEL_1;
    String quizKey = id;
    Boolean isFinalStage = IS_NOT_LAST_STAGE;
    String nextLevelId = WEBDEV_INPUT + LEVEL_2;

    GameStage expectedGameStage = new GameStage(name, content, id, quizKey, isFinalStage, nextLevelId);
    this.gameStageDatabase.storeGameStage(expectedGameStage);

    GameStage resultGameStage = this.gameStageDatabase.getGameStage(WEBDEV_INPUT, 1);

    // convert to JsonElement for deep comparison
    String result = resultGameStage.getContent();
    String expected = expectedGameStage.getContent();
    Assert.assertEquals(result, expected);
  }

    /** Tests that the objects that queried from the database sucessfully and compare the content by getting a gameStage*/
  @Test
  public void getGameStageFromPlayerID_OutputsworksSuccessfully() throws Exception {
    String nameP = NAME_PLAYER;
    String email = EMAIL_PLAYER;
    String id = WEBDEV_INPUT + LEVEL_1;
    Player admin = new Player(nameP, email);
    admin.setCurrentPageID(id);

    this.playerDatabase.addPlayerToDatabase(admin);

    String name = WEBDEV_LEVEL1_NAME;
    String content = TEST_CONTENT;
    String quizKey = id;
    Boolean isFinalStage = IS_NOT_LAST_STAGE;
    String nextLevelId = WEBDEV_INPUT + LEVEL_2;

    GameStage expectedGameStage = new GameStage(name, content, id, quizKey, isFinalStage, nextLevelId);
    this.gameStageDatabase.storeGameStage(expectedGameStage);
    try{
      GameStage currentGameStageForPlayer = gameStageDatabase.getGameStage(admin.getCurrentPageID());
      // convert to JsonElement for deep comparison
      String result = currentGameStageForPlayer.getContent();
      String expected = TEST_CONTENT;
      Assert.assertEquals(result, expected);
    } catch(Exception e) {
        throw new Exception("The PlayerDatabase does not hold  the players curentPageID");
    }
  }
}
