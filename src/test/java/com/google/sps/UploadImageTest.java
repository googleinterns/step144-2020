package com.google.sps;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.Player;
import com.google.sps.servlets.UploadImageServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Entity;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.RuntimeException;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import java.io.PrintWriter;
import java.io.StringWriter;

// Responsible for testing the UploadImage and ImageHandler Servlets to make sure 
// They create accurate links.
@RunWith(JUnit4.class)
public final class UploadImageTest {
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final String EXPECTED_JSON_OUTPUT = "http://localhost:8080/_ah/upload/";
  private static UploadImageServlet uploadImageServlet;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before 
  public void setUp() {
      helper.setUp();
      MockitoAnnotations.initMocks(this);
      this.uploadImageServlet = this.newUploadImageServlet();
  }
  
  //Creating local blobstore service
  public UploadImageServlet newUploadImageServlet() {
      BlobstoreService localBlobstore = BlobstoreServiceFactory.getBlobstoreService();
      UploadImageServlet uploadImageServlet = new UploadImageServlet();
      return uploadImageServlet;
  }

  @Test
  public void uploadsProperly() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter  = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.uploadImageServlet.doGet(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(EXPECTED_JSON_OUTPUT));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}