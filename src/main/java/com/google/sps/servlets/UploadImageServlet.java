
package com.google.sps.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.blobstore.BlobKey;
import javax.servlet.annotation.WebServlet;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import javax.activation.DataSource;

// Servlet responsible for getting file and sending it to be handled. 
// Writes image url created by Blobstore
@WebServlet("/upload-image")
public class UploadImageServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private static final String IMAGE_HANDLER_SERVLET_PARAMETER = "/image-handler";
    private static final String TEXT_TYPE_PARAMETER = "text/html";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String uploadUrl = blobstoreService.createUploadUrl(IMAGE_HANDLER_SERVLET_PARAMETER);
        response.setContentType(TEXT_TYPE_PARAMETER);
        response.getWriter().println(uploadUrl);
    }
}