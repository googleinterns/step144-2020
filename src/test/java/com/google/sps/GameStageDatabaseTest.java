package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the GameStageDatabase to ensure that it puts and queries entities correctly */
@RunWith(JUnit4.class)
public final class GameStageDatabaseTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private GameStageDatabase gameStageDatabase;
  private static final String WEBDEV_LEVEL1_NAME = "Web Developer Level 1";
  private static final String WEBDEV_INPUT = "Web Developer";
  private static final String SOFTWARE_ENGINEER_LEVEL2_NAME = "Software Engineer Level 2";
  private static final String SOFTWARE_ENGINEER_INPUT = "Software Engineer";
  private static final String LEVEL_1 = "1";
  private static final String LEVEL_2 = "2";
  private static final String TEST_CONTENT = "Some test content here.";
  private static final Boolean IS_NOT_LAST_STAGE = false;
  private static final Boolean IS_LAST_STAGE = true;

  private static final Gson gson = new Gson();

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.gameStageDatabase = new GameStageDatabase(localDatastore);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests that the objects that are put into the database without raising exceptions */
  @Test
  public void storeGameStage_worksSuccessfully() {
    String name1 = WEBDEV_LEVEL1_NAME;
    String content1 = TEST_CONTENT;
    String id1 = WEBDEV_INPUT + LEVEL_1;
    String quizKey1 = id1;
    Boolean isFinalStage1 = IS_NOT_LAST_STAGE;
    String nextLevelId1 = WEBDEV_INPUT + LEVEL_2;

    GameStage gameStage1 =
        new GameStage(name1, content1, id1, quizKey1, isFinalStage1, nextLevelId1);

    String name2 = SOFTWARE_ENGINEER_LEVEL2_NAME;
    String content2 = TEST_CONTENT;
    String id2 = SOFTWARE_ENGINEER_INPUT + LEVEL_2;
    String quizKey2 = id2;
    Boolean isFinalStage2 = IS_LAST_STAGE;
    String nextLevelId2 = null;

    GameStage gameStage2 =
        new GameStage(name2, content2, id2, quizKey2, isFinalStage2, nextLevelId2);
    this.gameStageDatabase.storeGameStage(gameStage1);
    this.gameStageDatabase.storeGameStage(gameStage2);
  }

  /** Tests that the objects that queried from the database sucessfully */
  @Test
  public void getGameStage_worksSuccessfully() {
    String name = WEBDEV_LEVEL1_NAME;
    String content = TEST_CONTENT;
    String id = WEBDEV_INPUT + LEVEL_1;
    String quizKey = id;
    Boolean isFinalStage = IS_NOT_LAST_STAGE;
    String nextLevelId = WEBDEV_INPUT + LEVEL_2;

    GameStage expectedGameStage =
        new GameStage(name, content, id, quizKey, isFinalStage, nextLevelId);

    this.gameStageDatabase.storeGameStage(expectedGameStage);
    GameStage resultGameStage = this.gameStageDatabase.getGameStage(WEBDEV_INPUT, 1);

    // convert to JsonElement for deep comparison
    JsonElement result = JsonParser.parseString(gson.toJson(resultGameStage));
    JsonElement expected = JsonParser.parseString(gson.toJson(expectedGameStage));
    Assert.assertEquals(result, expected);
  }
}
