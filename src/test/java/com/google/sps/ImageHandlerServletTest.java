package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.ImageHandlerServlet;
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

// Responsible for testing the ImageHandler Servlets to make sure
// They create accurate links.
@RunWith(JUnit4.class)
public final class ImageHandlerServletTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final String EXPECTED_JSON_OUTPUT_TRUE = "true";
  private static final String EXPECTED_JSON_OUTPUT_FALSE = "false";
  private static ImageHandlerServlet imageHandlerServlet;
  private static BlobstoreService localBlobstore = BlobstoreServiceFactory.getBlobstoreService();
  private static boolean isLoggedIn = false;
  private static PlayerDatabase playerDatabase;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    this.imageHandlerServlet = this.newImageHandlerServlet();
  }
  // Creating local handle servlet
  public ImageHandlerServlet newImageHandlerServlet() {
    ImageHandlerServlet imageHandlerServlet = new ImageHandlerServlet();
    return imageHandlerServlet;
  }

  @Test
  public void doGetImageHandlerTest() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.imageHandlerServlet.doGet(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertTrue(
        result.contains(EXPECTED_JSON_OUTPUT_TRUE) || result.contains(EXPECTED_JSON_OUTPUT_FALSE));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
