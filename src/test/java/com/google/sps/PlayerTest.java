package com.google.sps;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.servlets.UserAuthServlet;
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
import java.io.PrintWriter;
import java.io.StringWriter;

// Responsible for testing the UserAuthServlet to make sure that players can log in and log out
// of the webpage.
@RunWith(JUnit4.class)
public final class PlayerTest {
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final User TEST_USER = new User("test@gmail.com", "gmail.com");
  private static final String TEST_NAME_1 = "Name 1";
  private static final String TEST_EMAIL_1 = "Email 1";
  private static final String TEST_ID_1 = "Id 1";
  private static final String EXPECTED_JSON_OUTPUT = "<p>Login <a href=\"/_ah/login?continue=%2Findex.html\">here</a>.</p>";
  private static UserAuthServlet userAuthServlet;
  private static Player player = new Player(TEST_NAME_1, TEST_EMAIL_1);
  private static PlayerDatabase playerDatabase;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before 
  public void setUp() {
      helper.setUp();
      MockitoAnnotations.initMocks(this);
      this.userAuthServlet = this.newUserAuthServlet();
  }
  
  //Creates local datastore
  public UserAuthServlet newUserAuthServlet() {
      DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
      playerDatabase = new PlayerDatabase(localDatastore);
      playerDatabase.addPlayerToDatabase(player);
      UserAuthServlet userAuthServlet = new UserAuthServlet(playerDatabase);
      return userAuthServlet;
  }

  @Test
  public void createsNewEntity() throws IOException {
    player.setID(TEST_ID_1);
    player.setImageID("Image id");
    player.setCurrentPageID("CurrentPageID");
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter  = new PrintWriter(stringWriter);
    when(this.response.getWriter()).thenReturn(printWriter);
        
    this.userAuthServlet.doGet(this.request, this.response);

    String result = stringWriter.toString();
    Assert.assertTrue(result.contains(EXPECTED_JSON_OUTPUT));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}