package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.UserAuthServlet;
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

// Responsible for testing the UserAuthServlet to make sure that players can log in and log out
// of the webpage.
@RunWith(JUnit4.class)
public final class UserAuthServletTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final User TEST_USER = new User("test@gmail.com", "gmail.com");
  private static final String TEST_NAME_1 = "Name 1";
  private static final String TEST_EMAIL_1 = "Email 1";
  private static final String TEST_IMAGE_ID_1 = "ImageID 1";
  private static final String TEST_ID_1 = "Id 1";
  private static final String EXPECTED_JSON_OUTPUT_LOGIN = "<p>Login <a href=";
  private static final String EXPECTED_JSON_OUTPUT_LOGOUT = "<p>Logout <a href=";
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static UserAuthServlet userAuthServlet;
  private static Player player = new Player(TEST_NAME_1, TEST_EMAIL_1, TEST_IMAGE_ID_1);
  private static PlayerDatabase playerDatabase;
  private static User user = null;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    this.userAuthServlet = this.newUserAuthServlet();
  }

  // Creates local datastore
  public UserAuthServlet newUserAuthServlet() {
    UserAuthServlet userAuthServlet = new UserAuthServlet();
    return userAuthServlet;
  }

  @Test
  public void doGetTest_UserAuthServlet() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
    this.userAuthServlet.doGet(this.request, this.response);
    String result = stringWriter.toString();
    Assert.assertTrue(
        result.contains(EXPECTED_JSON_OUTPUT_LOGIN)
            || result.contains(EXPECTED_JSON_OUTPUT_LOGOUT));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
