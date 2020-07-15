package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import com.google.sps.data.Player;
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

@RunWith(JUnit4.class)
public final class GetGameDialogueTest {
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static String QUERY_FOR_GAMESTAGE_ENTITY = "gamestage";
  private static final String SOFTWARE_ENGINEER_LEVEL1_NAME = "Software Engineer Level 1";
  private static final String SOFTWARE_ENGINEER_INPUT = "Software Engineer";
  private static final String WEBDEV_LEVEL1_NAME = "Web Developer Level 1";
  private static final String WEBDEV_INPUT = "Web Developer";
  private static final String LEVEL_1 = "1";
  private static final String LEVEL_2 = "2";
  private static final String QUIZ_ID = "quizkey";
  private static final String IMG_ID = "image";
  private static final String PLAYER_ID = "Entitie ID";
  private static final String TEST_CONTENT = "Some test content here.";
  private static final Boolean IS_NOT_LAST_STAGE = false;
  private static final Boolean IS_LAST_STAGE = true;
  private static final String NAME_PLAYER = "many_admin";
  private static final String EMAIL_PLAYER = "admin@google.com";
  private GameStageDatabase gameStageDatabase;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp(); // initialize local datastore for testing
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.gameStageDatabase = new GameStageDatabase(localDatastore);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /**
   * Tests that the GameStage is store sucessfully and can be queried from the database sucessfully
   */
  @Test
  public void get_GameStageFrom_Id_CompareContent_succeeds() {
    String name = WEBDEV_LEVEL1_NAME;
    String content = TEST_CONTENT;
    String id = WEBDEV_INPUT + LEVEL_1;
    String quizKey = QUIZ_ID;
    Boolean isFinalStage = IS_NOT_LAST_STAGE;
    String nextLevelId = WEBDEV_INPUT + LEVEL_2;

    GameStage expectedGameStage =
        new GameStage(name, content, id, quizKey, isFinalStage, nextLevelId);
    gameStageDatabase.storeGameStage(expectedGameStage);

    GameStage resultGameStage = this.gameStageDatabase.getGameStage(WEBDEV_INPUT, 1);

    String result = resultGameStage.getContent();
    String expected = expectedGameStage.getContent();
    Assert.assertEquals(result, expected);
  }

  /**
   * Tests that the objects that queried from the database sucessfully and compare the content by
   * getting a gameStage
   */
  @Test
  public void doGet_gameStageFromPlayerId_succeeds() throws Exception {
    String nameP = NAME_PLAYER;
    String email = EMAIL_PLAYER;
    String id = PLAYER_ID;
    String img_id = IMG_ID;
    String currentPageID = WEBDEV_INPUT + LEVEL_1;
    Player admin = new Player(nameP, email, id, img_id, currentPageID);

    String name = WEBDEV_LEVEL1_NAME;
    String content = TEST_CONTENT;
    String quizKey = QUIZ_ID;
    Boolean isFinalStage = IS_NOT_LAST_STAGE;
    String nextLevelId = WEBDEV_INPUT + LEVEL_2;

    GameStage expectedGameStage =
        new GameStage(name, content, currentPageID, quizKey, isFinalStage, nextLevelId);
    gameStageDatabase.storeGameStage(expectedGameStage);
    GameStage currentGameStageForPlayer = gameStageDatabase.getGameStage(admin.getCurrentPageID());

    String result = currentGameStageForPlayer.getContent();
    String expected = TEST_CONTENT;
    Assert.assertEquals(result, expected);
  }
}
