package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is responsible for accessing the files that the user has uploaded to the blobstore.
 */
@WebServlet("/image-handler")
public class ImageHandlerServlet extends HttpServlet {
  private static BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private static String displayName;
  private static String imageID;
  private static final String CURRENT_PLAYER_TRUE_PARAMETER = "true";
  private static final String CURRENT_PLAYER_FALSE_PARAMETER = "false";
  private static final String DISPLAY_NAME_PARAMETER = "displayName";
  private static final String ILLEGAL_FILE_TYPE_MESSAGE =
      "Illegal file type, only processes image type files.";
  private static final String MESSAGE_PARAMETER = "message";
  private static final String EMAIL_PARAMETER = "email";
  private static final String EMPTY_PARAMETER = "empty";
  private static final String ID_PARAMETER = "id";
  private static final String IMAGE_ID_PARAMETER = "imageID";
  private static final String EXPERIENCE_POINTS_PARAMETER = "experiencePoints";
  private static final String PROMOTION_THRESHOLD_PARAMETER = "promotionThreshold";
  private static final String IMAGE_PARAMETER = "image";
  private static final String DEFAULT_PARAMETER = "default";
  private static final String PLAYER_QUERY_PARAMETER = "player";
  private static final String UPLOADED_REDIRECT_PARAMETER = "/careerquiz.html";
  private static final String LOGIN_REDIRECT_PARAMETER = "/userAuthPage.html";
  private static final String CHARACTER_DESIGN_REDIRECT_PARAMETER = "/characterDesign.html";
  private static final String CONTENT_TYPE = "text/html";
  private static final int NO_EXPERIENCE = 0;
  private static final int STARTER_THRESHOLD = 5;
  private static final String START_PAGE = "Character Design";
  private static final String START_ACCESSORY_FILEPATH = "images/accessories/SpinnerHat.png";
  private static final String START_ACCESSORY_TYPE = "HAT";
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  UserService userService = UserServiceFactory.getUserService();
  User user;
  boolean isLoggedIn;

  private void updateService() throws LoggedOutException {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.userService = UserServiceFactory.getUserService();
    this.user = userService.getCurrentUser();
    this.isLoggedIn = userService.isUserLoggedIn();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      updateService();
      String displayName = request.getParameter(MESSAGE_PARAMETER);
      String imageBlobKeyString = createUploadedBlobKey(request, IMAGE_PARAMETER);
      // create a new player from the currently logged in user and store it in PlayerDatabase
      Player player = newPlayer(imageBlobKeyString, displayName);
      PlayerDatabase playerDatabase = new PlayerDatabase(this.datastore, this.userService);
      playerDatabase.addPlayerToDatabase(player);
      response.sendRedirect(UPLOADED_REDIRECT_PARAMETER);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      response.sendRedirect(CHARACTER_DESIGN_REDIRECT_PARAMETER);
    } catch (LoggedOutException e) {
      response.sendRedirect(LOGIN_REDIRECT_PARAMETER);
    }
  }

  private Player newPlayer(String imageBlobKeyString, String displayName) {
    Player player =
        new Player(
            displayName,
            this.user.getEmail(),
            this.user.getUserId(),
            imageBlobKeyString,
            START_PAGE);
    player.setExperiencePoints(NO_EXPERIENCE);
    player.setPromotionThreshold(STARTER_THRESHOLD);
    player.setAllAccessoryIDs(Arrays.asList(START_ACCESSORY_FILEPATH + START_ACCESSORY_TYPE));
    return player;
  }

  public static String createUploadedBlobKey(
      HttpServletRequest request, String formInputElementName) {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return DEFAULT_PARAMETER;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

    // User submitted form without selecting a file, so we have a pointless blobKey. (live server)
    if (blobInfo.getSize() == 0) {
      return DEFAULT_PARAMETER;
    }

    // Checks that the file is the right type.
    if (!blobInfo.getContentType().contains(IMAGE_PARAMETER)) {
      blobstoreService.delete(blobKey);
      throw new IllegalArgumentException(ILLEGAL_FILE_TYPE_MESSAGE);
    }

    return blobKey.getKeyString();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String displayName = request.getParameter(MESSAGE_PARAMETER);

    response.getWriter();
    response.setContentType(CONTENT_TYPE);
    try {
      updateService();
      if (this.isLoggedIn) {
        handleLoggedInUser(response);
      } else {
        handleLoggedOutUser(response);
      }
    } catch (LoggedOutException e) {
      handleLoggedOutUser(response);
    }
    response.getWriter().println(imageID);
    response.getWriter().println(displayName);
  }

  private void handleLoggedInUser(HttpServletResponse response) throws IOException {
    Entity player = null;
    try {
      PlayerDatabase playerDatabase = new PlayerDatabase(this.datastore, this.userService);
      player = playerDatabase.getCurrentPlayerEntity();
    } catch (LoggedOutException e) {
    }
    imageID = player.getProperty(IMAGE_ID_PARAMETER).toString();
    displayName = player.getProperty(DISPLAY_NAME_PARAMETER).toString();
    response.getWriter().println(CURRENT_PLAYER_TRUE_PARAMETER);
  }

  private void handleLoggedOutUser(HttpServletResponse response) throws IOException {
    response.getWriter().println(CURRENT_PLAYER_FALSE_PARAMETER);
    imageID = EMPTY_PARAMETER;
    displayName = EMPTY_PARAMETER;
  }
}
