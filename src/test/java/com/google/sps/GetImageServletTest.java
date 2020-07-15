package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.servlets.GetImageServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

// Responsible for testing the GetImageServlet to make sure that the image
// being properly served.
@RunWith(JUnit4.class)
public final class GetImageServletTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalBlobstoreServiceTestConfig());
  private static GetImageServlet getImageServlet;
  private static final String BLOB_KEY_PARAMETER = "blobkey";
  private static final String TEST_KEY = "M-kUqkjD_Cr--tf0Ga26vg";
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    this.getImageServlet = this.newGetImageServlet();
  }

  // Creating local get servlet
  public GetImageServlet newGetImageServlet() {
    GetImageServlet getImageServlet = new GetImageServlet();
    return getImageServlet;
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Rule public ExpectedException emptyImageRule = ExpectedException.none();

  @Test
  public void doGet_successfulPath() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(request.getParameter(BLOB_KEY_PARAMETER)).thenReturn(TEST_KEY);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.getImageServlet.doGet(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(BLOB_KEY_PARAMETER));
  }

  @Test
  public void doGet_emptyImage_throwsIllegalArgumentException() throws IOException {
    String result = "";
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.request.getParameter(BLOB_KEY_PARAMETER)).thenReturn(null);
    when(this.response.getWriter()).thenReturn(printWriter);
    emptyImageRule.expect(IllegalArgumentException.class);
    this.getImageServlet.doGet(this.request, this.response);
  }
}
