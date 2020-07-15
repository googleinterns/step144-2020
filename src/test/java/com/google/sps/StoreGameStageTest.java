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
import com.google.sps.servlets.StoreGameStage;
import java.io.IOException;
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

/** Tests that the StoreGameStage correctly adds static entities to the game datastore */
@RunWith(JUnit4.class)
public final class StoreGameStageTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private StoreGameStage storeGameStage;
  private GameStageDatabase gameStageDatabase;
  private static final String GAME_STAGE_FORM_SUBMIT_PARAMETER = "gameStageSubmit";
  private static final String GAME_STAGE_NAME_PARAMETER = "gameStageName";
  private static final String GAME_STAGE_CAREERPATH_PARAMETER = "gameStageCareerPath";
  private static final String GAME_STAGE_LEVEL_PARAMETER = "gameStageLevel";
  private static final String GAME_STAGE_CONTENT_PARAMETER = "gameStageContent";
  private static final String GAME_STAGE_IS_FINAL_PARAMETER = "gameStageIsFinal";
  private static final String YES_INPUT = "Yes";
  private static final String NO_INPUT = "No";
  private static final String WEBDEV_LEVEL1_NAME = "Web Developer Level 1";
  private static final String WEBDEV_INPUT = "Web Developer";
  private static final String LEVEL_1 = "1";
  private static final String LEVEL_2 = "2";
  private static final String TEST_CONTENT = "Some test content here.";
  private static final Boolean IS_NOT_LAST_STAGE = false;
  private static final Gson gson = new Gson();

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.gameStageDatabase = new GameStageDatabase(localDatastore);
    MockitoAnnotations.initMocks(this);
    this.storeGameStage = new StoreGameStage();
    this.storeGameStage.init();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests that the doPost method fetches form parameters, creates appropriate game stage object */
  @Test
  public void doPost_withValidInput_addsToDatabase() throws IOException {
    // mocks user entering in game stage data
    String nameInput = WEBDEV_LEVEL1_NAME;
    String careerPathInput = WEBDEV_INPUT;
    String levelInput = LEVEL_1;
    String contentInput = TEST_CONTENT;
    String isFinalStageInput = NO_INPUT;

    mockUserGameStageInput(nameInput, careerPathInput, levelInput, contentInput, isFinalStageInput);

    String name = WEBDEV_LEVEL1_NAME;
    String content = TEST_CONTENT;
    String id = WEBDEV_INPUT + LEVEL_1;
    String quizKey = id;
    Boolean isFinalStage = IS_NOT_LAST_STAGE;
    String nextLevelId = WEBDEV_INPUT + LEVEL_2;

    GameStage expectedGameStage =
        new GameStage(name, content, id, quizKey, isFinalStage, nextLevelId);

    this.storeGameStage.doPost(this.request, this.response);
    GameStage resultGameStage = this.gameStageDatabase.getGameStage(WEBDEV_INPUT, 1);

    // converting to JsonElements to do deep equality checks
    JsonElement result = JsonParser.parseString(gson.toJson(resultGameStage));
    JsonElement expected = JsonParser.parseString(gson.toJson(expectedGameStage));
    Assert.assertEquals(result, expected);
  }

  private void mockUserGameStageInput(
      String nameInput,
      String careerPathInput,
      String levelInput,
      String contentInput,
      String isFinalInput) {
    when(this.request.getParameter(GAME_STAGE_FORM_SUBMIT_PARAMETER))
        .thenReturn(GAME_STAGE_FORM_SUBMIT_PARAMETER);
    when(this.request.getParameter(GAME_STAGE_NAME_PARAMETER)).thenReturn(nameInput);
    when(this.request.getParameter(GAME_STAGE_CAREERPATH_PARAMETER)).thenReturn(careerPathInput);
    when(this.request.getParameter(GAME_STAGE_LEVEL_PARAMETER)).thenReturn(levelInput);
    when(this.request.getParameter(GAME_STAGE_CONTENT_PARAMETER)).thenReturn(contentInput);
    when(this.request.getParameter(GAME_STAGE_IS_FINAL_PARAMETER)).thenReturn(isFinalInput);
  }
}
