package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private static String displayName;
  private static String imageID;
  private static final String CURRENT_PLAYER_TRUE_PARAMETER = "true";
  private static final String CURRENT_PLAYER_FALSE_PARAMETER = "false";
  private static final String DISPLAY_NAME_PARAMETER = "displayName";
  private static final String EMAIL_PARAMETER = "email";
  private static final String ID_PARAMETER = "id";
  private static final String IMAGE_PARAMETER = "image";
  private static final String JPEG_PARAMETER = ".jpeg";
  private static final String JPG_PARAMETER = ".jpg";
  private static final String PLAYER_QUERY_PARAMETER = "Player";
  private static final String PNG_PARAMETER = ".png";
  private static final String UPLOADED_REDIRECT_PARAMETER = "/uploaded.html";
  private static final String LOGIN_REDIRECT_PARAMETER = "/userAuthPage.html";
  private static final String DEFAULT_IMAGE_GS_LOCATION =
      "/gs/cs-career-step-2020.appspot.com/face.jpg";
  private static Entity currentPlayer = currentPlayer();
  private static boolean isLoggedIn = UserServiceFactory.getUserService().isUserLoggedIn();

  private static Entity currentPlayer() {
    Entity entity = new Entity("Player");
    try {
      entity = PlayerDatabase.getCurrentPlayerEntity();
    } catch (Exception e) {
    }
    return entity;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String imageUrl;
    try {
      if (!isLoggedIn) {
        response.sendRedirect(LOGIN_REDIRECT_PARAMETER);
      } else {
        // Get the displayName entered by the user.
        String displayName = request.getParameter(DISPLAY_NAME_PARAMETER);
        // Get the imageName to check if a file was uploaded.
        String imageName = request.getParameter(IMAGE_PARAMETER);
        if (imageName == null) { // Default image path
          BlobKey defaultBlobKey = blobstoreService.createGsBlobKey(DEFAULT_IMAGE_GS_LOCATION);
          imageUrl = getFileUrl(defaultBlobKey);
        } else {
          // Get the URL of the image that the user uploaded to Blobstore.
          BlobKey uploadedBlobKey = createUploadedBlobKey(request, IMAGE_PARAMETER);
          imageUrl = getFileUrl(uploadedBlobKey);
        }
        // Assign imageUrl to current player
        PlayerDatabase.setEntityImageID(imageUrl);

        // Assign displayName to current player
        PlayerDatabase.setEntityDisplayName(displayName);
      }
      response.sendRedirect(UPLOADED_REDIRECT_PARAMETER);
    } catch (Exception e) {
    }
  }

  // Gets the URL of the uploaded file; null if no file was uploaded
  private BlobKey createUploadedBlobKey(HttpServletRequest request, String formInputElementName) {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);
    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);
    return blobKey;
  }

  private String getFileUrl(BlobKey blobKey) {
    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    // To support running in Google Cloud Shell with AppEngine's dev server,
    // we must use the relativepath to the image, rather than
    // the path returned by imagesService which contains a host.
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
    try {
      response.getWriter();
      if (isLoggedIn) {
        Player player = new Player("", "");
        PlayerDatabase.addPlayerToDatabase(player);
        imageID = PlayerDatabase.getEntityImageID();
        displayName = PlayerDatabase.getEntityDisplayName();
        response.getWriter().println(CURRENT_PLAYER_TRUE_PARAMETER);
      } else {
        response.getWriter().println(CURRENT_PLAYER_FALSE_PARAMETER);
      }
      response.getWriter().println(imageID);
      response.getWriter().println(displayName);
    } catch (Exception e) {
    }
  }
}
