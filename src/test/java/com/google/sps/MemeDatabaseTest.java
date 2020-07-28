package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.sps.data.Meme;
import com.google.sps.data.MemeDatabase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Responsible for testing that the MemeDatabase correctly stores and retreives entities */
@RunWith(JUnit4.class)
public final class MemeDatabaseTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private MemeDatabase memeDatabase;
  private static final Gson gson = new Gson();
  private static final String ID_1 = "123456";
  private static final String ID_2 = "234567";
  private static final String ID_3 = "345678";
  private static final String SOURCE_1 = "images/dir/source1.png";
  private static final String SOURCE_2 = "images/source2.png";
  private static final String SOURCE_3 = "images/source3.png";
  private static final String TITLE_1 = "testTitle1";
  private static final String TITLE_2 = "testTitle2";
  private static final String TITLE_3 = "testTitle3";

  @Before
  public void setUp() {
    // Initialize local datastore
    helper.setUp();
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.memeDatabase = new MemeDatabase(localDatastore);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Rule public ExpectedException entityNotStored = ExpectedException.none();

  @Test
  public void addMemeToDatabase_successfulPath() {
    // Tests addMemeToDatabase, memeToEntity
    Meme meme1 = new Meme(TITLE_1, SOURCE_1, ID_1);
    Meme meme2 = new Meme(TITLE_2, SOURCE_2, ID_2);

    this.memeDatabase.addMemeToDatabase(meme1);
    this.memeDatabase.addMemeToDatabase(meme2);

    Boolean meme1Added = this.memeDatabase.memes.contains(meme1);
    Boolean meme2Added = this.memeDatabase.memes.contains(meme2);
    Assert.assertTrue(meme1Added && meme2Added);
  }

  @Test
  public void getMemeFromID_successfulPath() throws EntityNotFoundException {
    Meme meme1 = new Meme(TITLE_1, SOURCE_1, ID_1);

    this.memeDatabase.addMemeToDatabase(meme1);

    Meme retrieved1 = this.memeDatabase.getMemeFromID(ID_1);

    JsonElement result1 = JsonParser.parseString(gson.toJson(retrieved1));
    JsonElement expected1 = JsonParser.parseString(gson.toJson(meme1));
    Assert.assertEquals(result1, expected1);
  }

  @Test
  public void queriedEntityNotStored_throwsEntityNotFoundException()
      throws EntityNotFoundException {
    Meme meme3 = new Meme(TITLE_1, SOURCE_3, ID_3);
    entityNotStored.expect(EntityNotFoundException.class);
    Entity entity = this.memeDatabase.getEntityFromID(ID_3);
  }
}
