package com.googl.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the user submits the form, Blobstore processes the file upload and then forwards the request
 * to this servlet. This servlet can then process the request using the file URL we get from
 * Blobstore.
 */
@WebServlet("/image-handler")
public class ImageHandlerServlet extends HttpServlet {
  private static String displayName;
  private static String imageID;
  private static final String DISPLAY_NAME_PARAMETER = "displayName";
  private static final String EMAIL_PARAMETER = "email";
  private static final String ID_PARAMETER = "id";
  private static final String IMAGE_PARAMETER = "image";
  private static final String JPEG_PARAMETER = ".jpeg";
  private static final String JPG_PARAMETER = ".jpg";
  private static final String PLAYER_QUERY_PARAMETER = "Player";
  private static final String PNG_PARAMETER = ".png";
  private static final String UPLOADED_REDIRECT_PARAMETER = "/uploaded.html";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the displayName entered by the user.
    String displayName = request.getParameter(DISPLAY_NAME_PARAMETER);

    // Get the URL of the image that the user uploaded to Blobstore.
    String imageUrl = getUploadedFileUrl(request, IMAGE_PARAMETER);

    Player currentPlayer = getCurrentPlayer();
    if (currentPlayer != null) {
      // Assign imageUrl to current player
      currentPlayer.setImageID(imageUrl);

      // Assign displayName to current player
      currentPlayer.setDisplayName(displayName);
    }

    response.sendRedirect(UPLOADED_REDIRECT_PARAMETER);
    ArrayList<Player> Players = PlayerDatabase.getPlayers();
  }

  // Gets the URL of the uploaded file; null if no file was uploaded
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);
    // User submitted form without selecting a file
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }
    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);
    // User submitted form without selecting a file (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }
    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    // To support running in Google Cloud Shell with AppEngine's dev server,
    // we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }

  private boolean isValidImage(String imageUrl) {
    if (imageUrl.endsWith(JPG_PARAMETER)
        || imageUrl.endsWith(JPEG_PARAMETER)
        || imageUrl.endsWith(PNG_PARAMETER)) {
      return true;
    }
    return false;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request != null && response != null) {
      response.getWriter();
      Player currentPlayer = getCurrentPlayer();
      if (currentPlayer != null) {
        imageID = currentPlayer.getImageID();
        displayName = currentPlayer.getDisplayName();
      }
      response.getWriter().println(imageID);
      response.getWriter().println(displayName);
    }
  }

  // Uses the email of the current user logged in and comares it to
  // emails in the Player Database. Returns null if no entity is found.
  private Player getCurrentPlayer() {
    if (UserServiceFactory.getUserService().getCurrentUser() != null) {
      User currentUser = UserServiceFactory.getUserService().getCurrentUser();
      String email = currentUser.getEmail();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query =
          new Query(PLAYER_QUERY_PARAMETER)
              .setFilter(
                  new Query.FilterPredicate(
                      ID_PARAMETER, Query.FilterOperator.EQUAL, currentUser.getUserId()));
      Key playerKey;
      PreparedQuery results = datastore.prepare(query);
      for (Entity entity : results.asIterable()) {
        if (email.equals(entity.getProperty(EMAIL_PARAMETER).toString())) {
          playerKey = entity.getKey();
        }
      }
    }
    return null;
  }
}
