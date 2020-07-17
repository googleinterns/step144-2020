package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the user submits the form, Blobstore processes the file upload and then forwards the request
 * to this servlet. This servlet then returns the image fetched from Blobstore.
 */
@WebServlet("/get-image")
public class GetImageServlet extends HttpServlet {
  private static final String BLOB_KEY_PARAMETER = "blobkey";
  private BlobstoreService blobstoreService;
  private static final String TEXT_TYPE_PARAMETER = "text/html";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    blobstoreService = initializeBlobstore();
    BlobKey blobkey = new BlobKey(request.getParameter(BLOB_KEY_PARAMETER));
    blobstoreService.serve(blobkey, response);
    response.setContentType(TEXT_TYPE_PARAMETER);
    response.getWriter().println(BLOB_KEY_PARAMETER);
  }

  public BlobstoreService initializeBlobstore() {
    return BlobstoreServiceFactory.getBlobstoreService();
  }
}
