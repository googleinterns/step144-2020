package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.ImageHandlerServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
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

// Responsible for testing the ImageHandler Servlets to make sure
// They create accurate links.
@RunWith(JUnit4.class)
public final class ImageHandlerServletTest {
  private static final String HANDLE_LOGGED_OUT_USER = "false\nempty\nnull\n";
  private final String EMAIL = "email";
  private final String AUTH_DOMAIN = "email.com";
  private final String CURR_USER_ID = "testid";
  private String imageID = "testImageIDString";
  private Map<String, Object> USER_ID_CONFIG = new HashMap<>();

  {
    USER_ID_CONFIG.put("com.google.appengine.api.users.UserService.user_id_key", CURR_USER_ID);
  }
  // creating a test user service, setting current user to be a logged in admin
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
  private final String EXPECTED_OUTPUT_TRUE = "true";
  private final String EXPECTED_OUTPUT_FALSE = "false";
  private final String EXPECTED_OUTPUT_DEFAULT = "default";
  private final String EXPECTED_OUTPUT_EMPTY = "empty";
  private final String IMAGE_PARAMETER = "image";
  private final String IMAGE_ID_PARAMETER = "imageID";
  private String imageBlobKeyString;
  private ImageHandlerServlet imageHandlerServlet;
  private BlobstoreService localBlobstore = BlobstoreServiceFactory.getBlobstoreService();
  private boolean isLoggedIn = false;
  private DatastoreService datastore;
  private UserService userService;
  private PlayerDatabase playerDatabase;
  private Player player;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private BlobInfo blobInfo;

  @Before
  public void setUp() throws LoggedOutException {
    helper.setUp();
    this.userService = UserServiceFactory.getUserService();
    this.imageHandlerServlet = this.newImageHandlerServlet();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.playerDatabase = new PlayerDatabase(datastore, userService);
    this.player =
        createCurrentPlayerAndAddToDatabase(
            playerDatabase, userService.getCurrentUser(), "DisplayName", "GameStage");
    this.imageBlobKeyString = "";
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  // Creating local handle servlet
  public ImageHandlerServlet newImageHandlerServlet() {
    ImageHandlerServlet imageHandlerServlet = new ImageHandlerServlet();
    return imageHandlerServlet;
  }

  private Player createCurrentPlayerAndAddToDatabase(
      PlayerDatabase playerDatabase,
      User currentUser,
      String displayName,
      String currentGameStageId) {
    Player player = new Player(displayName, currentUser.getEmail(), imageID);
    player.setID(currentUser.getUserId());
    player.setImageID(imageID);
    player.setCurrentPageID(currentGameStageId);
    this.playerDatabase.addPlayerToDatabase(player);
    return player;
  }

  @Rule public ExpectedException loggedOutExceptionRule = ExpectedException.none();
  @Rule public ExpectedException wrongFileTypeExceptionRule = ExpectedException.none();

  @Test
  public void doGet_successfulpath() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.imageHandlerServlet.doGet(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(EXPECTED_OUTPUT_TRUE));
  }

  @Test
  public void doGet_wrongFileType_throwsNullPointerException() throws IOException {
    String result = "";
    when(blobInfo.getContentType()).thenReturn(EXPECTED_OUTPUT_DEFAULT);
    wrongFileTypeExceptionRule.expect(NullPointerException.class);
    this.imageHandlerServlet.doGet(this.request, this.response);
  }

  @Test
  public void doGet_emptyImage() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.blobInfo.getSize()).thenReturn(0L);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.imageHandlerServlet.doGet(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(EXPECTED_OUTPUT_TRUE));
  }

  @Test
  public void doGet_whileLoggedIn() throws IOException {
    helper.setEnvIsLoggedIn(true);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.imageHandlerServlet.doGet(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(EXPECTED_OUTPUT_TRUE));
  }

  @Test
  public void doGet_handleLoggedOutUser() throws IOException {
    helper.setEnvIsLoggedIn(false);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.imageHandlerServlet.doGet(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertEquals(result, HANDLE_LOGGED_OUT_USER);
  }
}
