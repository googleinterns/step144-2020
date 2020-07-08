package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.sps.data.QuestionChoice;
import com.google.sps.data.QuestionDatabase;
import com.google.sps.data.QuizQuestion;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the QuestionDatabase to ensure that it puts and queries entities correctly */
@RunWith(JUnit4.class)
public final class QuestionDatabaseTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private QuestionDatabase questionDatabase;
  private static final Gson gson = new Gson();
  private static final String CAREER_1 = "career1";
  private static final String CAREER_2 = "career2";
  private static final String CHOICE_1 = "choice1";
  private static final String CHOICE_2 = "choice2";
  private static final String QUESTION = "A question";
  private static final String ENTITY_QUERY_STRING = "careerQuizQuestionAndChoices";
  private static final List<QuestionChoice> HARD_CODED_CHOICES =
      Arrays.asList(new QuestionChoice(CHOICE_1, CAREER_1), new QuestionChoice(CHOICE_2, CAREER_2));
  private static final QuizQuestion HARD_CODED_QUIZ_QUESTION =
      new QuizQuestion(QUESTION, HARD_CODED_CHOICES);
  private static final List<QuizQuestion> HARD_CODED_QUIZ_QUESTIONS =
      Arrays.asList(HARD_CODED_QUIZ_QUESTION);

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.questionDatabase = new QuestionDatabase(localDatastore, ENTITY_QUERY_STRING);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /**
   * Tests that the objects that are put into the database as entities are queried back as the same
   * objects
   */
  @Test
  public void testPutAndQueryIntoDatabase() {
    this.questionDatabase.putQuizQuestionsIntoDatabase(
        new QuizQuestion(QUESTION, HARD_CODED_CHOICES));
    List<QuizQuestion> expected = HARD_CODED_QUIZ_QUESTIONS;
    List<QuizQuestion> result = this.questionDatabase.getQuizQuestions();
    Assert.assertEquals(expected.size(), result.size());
    for (int i = 0; i < expected.size(); i++) {
      Assert.assertEquals(expected.get(i).getQuestion(), result.get(i).getQuestion());
    }
  }
}
