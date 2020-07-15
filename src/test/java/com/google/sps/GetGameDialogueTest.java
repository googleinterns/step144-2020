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

@RunWith(JUnit4.class)
public final class GetGameDialogueTest {
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private final LocalServiceTestHelper helper = 
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final Gson gson = new Gson();
  private static String QUERY_FOR_GAMESTAGE_ENTITY = "gamestage";
  private static final String SOFTWARE_ENGINEER_LEVEL1_NAME = "Software Engineer Level 1";
  private static final String SOFTWARE_ENGINEER_INPUT = "Software Engineer";
  private static final String LEVEL_1 = "1";
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
  private GameStageDatabase gameStageDatabase;
  private PlayerDatabase playerDatabase;

  @Before
  public void setUp() {
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
  public void storeGameStage_worksSuccessfully() {

    String name1 = SOFTWARE_ENGINEER_LEVEL1_NAME;
    String content1 = TEST_CONTENT;
    String id1 = SOFTWARE_ENGINEER_INPUT + LEVEL_1;
    String quizKey1 = id1;
    Boolean isFinalStage1 = IS_LAST_STAGE;
    String nextLevelId1 = null;
    //this was amended
    GameStage gameStage1 =
        new GameStage(name1, content1, id1, quizKey1, isFinalStage1, nextLevelId1);
    this.gameStageDatabase.storeGameStage(gameStage1);
  }

  /**
   * Tests that the doGet method returns JSON containing database queried career question and
   * choices

  @Test
  public void testGetGameDialogue_Outputs() throws IOException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    when(this.response.getWriter()).thenReturn(printWriter);

    // mocks the result of querying the GetGameDialogue
    this.getGameDialogue.doGet(this.request, this.response);
    // checks that the string writer used in servlet mock response contains the database object JSON
    // that matches with the hardcoded CareerQAndChoice given be the mock database
    Gson gson = new Gson();
    JsonElement expected = JsonParser.parseString(gson.toJson(TEST_STRING));
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(expected, result);
  }*/
}
