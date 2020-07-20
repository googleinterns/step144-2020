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
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
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
  private static final String IMAGE_PARAMETER = "image";
  private static final String DEFAULT_PARAMETER = "default";
  private static final String PLAYER_QUERY_PARAMETER = "player";
  private static final String UPLOADED_REDIRECT_PARAMETER = "/careerquiz.html";
  private static final String LOGIN_REDIRECT_PARAMETER = "/userAuthPage.html";
  private static final String CHARACTER_DESIGN_REDIRECT_PARAMETER = "/characterDesign.html";
  private static final String CONTENT_TYPE = "text/html";
  private final User USER = UserServiceFactory.getUserService().getCurrentUser();
  private Entity player;
  private DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
  private boolean isLoggedIn = UserServiceFactory.getUserService().isUserLoggedIn();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String displayName = request.getParameter(MESSAGE_PARAMETER);

    UserService userService = UserServiceFactory.getUserService();
    if (!isLoggedIn) {
      response.sendRedirect(LOGIN_REDIRECT_PARAMETER);
      return;
    }
    try {
      String imageBlobKeyString = createUploadedBlobKey(request, IMAGE_PARAMETER);
      // create and store store current player
      DATASTORE.put(newPlayerEntity(imageBlobKeyString, displayName));
      response.sendRedirect(UPLOADED_REDIRECT_PARAMETER);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      response.sendRedirect(CHARACTER_DESIGN_REDIRECT_PARAMETER);
      return;
    }
  }

  private Entity newPlayerEntity(String imageBlobKeyString, String displayName) {
    player = new Entity(PLAYER_QUERY_PARAMETER);
    player.setProperty(EMAIL_PARAMETER, USER.getEmail());
    player.setProperty(IMAGE_ID_PARAMETER, imageBlobKeyString);
    player.setProperty(DISPLAY_NAME_PARAMETER, displayName);
    player.setProperty(ID_PARAMETER, USER.getUserId());
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
    response.getWriter();
    response.setContentType(CONTENT_TYPE);
    if (isLoggedIn) {
      handleLoggedInUser(response);
    } else {
      handleLoggedOutUser(response);
    }
    response.getWriter().println(imageID);
    response.getWriter().println(displayName);
  }

  private void handleLoggedInUser(HttpServletResponse response) throws IOException {
    PlayerDatabase playerDatabase = new PlayerDatabase(DATASTORE);
    try {
      player = playerDatabase.getCurrentPlayerEntity();
    } catch (Exception e) {
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
