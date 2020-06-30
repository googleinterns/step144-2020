package com.google.sps;

import com.google.sps.servlets.CareerQuizServlet;
import com.google.sps.data.CareerQuestionDatabase;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Arrays;
import com.google.sps.data.CareerQuestionChoice;
import com.google.sps.data.CareerQuestionAndChoices;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.Guice;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/** Tests the CareerQuiz servlet and its interactions with CareerQuestionDatabase */
@RunWith(JUnit4.class)
public final class CareerQuizTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private CareerQuestionDatabase careerQuestionDatabase;

  private static final List<CareerQuestionChoice> HARD_CODED_CHOICES =
      Arrays.asList(
          new CareerQuestionChoice("choice1", "career1"),
          new CareerQuestionChoice("choice2", "career2"));
  private static final String DATABASE_OBJECT_JSON =
      "[{\"question\":\"A question\",\"choices\":"
          + "[{\"choiceText\":\"choice1\",\"associatedCareerPath\":\"career1\"},"
          + "{\"choiceText\":\"choice2\",\"associatedCareerPath\":\"career2\"}]}]";

  private void createInjector() {
    Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    this.createInjector();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCareerQuizServlet_OutputsJsonDatabaseQuestionAndChoices() throws IOException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    when(this.response.getWriter()).thenReturn(printWriter);

    // mocks the result of querying the CareerQuestionDatabase for all the Career Question and
    // choices
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    careerQuestionDatabase = new CareerQuestionDatabase(localDatastore);
    careerQuestionDatabase.putCareerQuestionAndChoicesIntoDatabase(
        new CareerQuestionAndChoices("A question", HARD_CODED_CHOICES));
    CareerQuizServlet careerQuizServlet = new CareerQuizServlet(careerQuestionDatabase);
    careerQuizServlet.doGet(this.request, this.response);
    // checks that the string writer used in servlet mock response contains the database object JSON
    // that matches with the hardcoded CareerQAndChoice given be the mock database
    Assert.assertTrue(stringWriter.toString().contains(DATABASE_OBJECT_JSON));
  }
}
