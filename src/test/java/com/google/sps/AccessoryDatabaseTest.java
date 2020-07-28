package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.sps.data.Accessory;
import com.google.sps.data.Accessory.Type;
import com.google.sps.data.AccessoryDatabase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the AccessoryDatabase to ensure that it puts and queries entities correctly */
@RunWith(JUnit4.class)
public final class AccessoryDatabaseTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private AccessoryDatabase accessoryDatabase;
  private static final String ID_1 = "id1";
  private static final String ID_2 = "id2";
  private static final String ID_3 = "id3";
  private static final String FILEPATH_1 = "dir/dir/filepath1.png";
  private static final String FILEPATH_2 = "dir/filepath2.png";
  private static final String NO_ACCESSORIES_MATCH_EXCEPTION =
      "No accessories have entities which match ";
  private static final Type HAT = Type.HAT;
  private static final Type GLASSES = Type.GLASSES;
  private static final int PIXEL_5 = 5;
  private static final int PIXEL_20 = 20;
  private static final int PIXEL_40 = 40;
  private static final int PIXEL_100 = 100;

  private static final Gson gson = new Gson();

  @Rule public ExpectedException notInDatabaseExceptionRule = ExpectedException.none();

  @Before
  public void setUp() {
    helper.setUp(); // initialize local datastore for testing
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.accessoryDatabase = new AccessoryDatabase(localDatastore);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests that the objects that are put into the database */
  @Test
  public void storeAccessory_worksSuccessfully() throws EntityNotFoundException {
    String id = ID_1;
    String filepath = FILEPATH_1;
    Type type = HAT;
    int height = PIXEL_100;
    int width = PIXEL_20;
    int xPos = PIXEL_40;
    int yPos = PIXEL_5;

    Accessory accessory1 = new Accessory(id, filepath, type, height, width, xPos, yPos);

    String id2 = ID_2;
    String filepath2 = FILEPATH_2;
    Type type2 = GLASSES;
    int height2 = PIXEL_40;
    int width2 = PIXEL_5;
    int xPos2 = PIXEL_40;
    int yPos2 = PIXEL_100;

    Accessory accessory2 = new Accessory(id2, filepath2, type2, height2, width2, xPos2, yPos2);

    this.accessoryDatabase.storeAccessory(accessory1);
    this.accessoryDatabase.storeAccessory(accessory2);
  }

  /** Tests that the objects that queried from the database sucessfully */
  @Test
  public void getAccessory_worksSuccessfully() throws EntityNotFoundException {
    String id = ID_1;
    String filepath = FILEPATH_1;
    Type type = HAT;
    int height = PIXEL_100;
    int width = PIXEL_20;
    int xPos = PIXEL_40;
    int yPos = PIXEL_5;

    Accessory expectedAccessory = new Accessory(id, filepath, type, height, width, xPos, yPos);

    this.accessoryDatabase.storeAccessory(expectedAccessory);
    Accessory resultAccessory = this.accessoryDatabase.getAccessory(ID_1);

    // convert to JsonElement for deep comparison
    JsonElement result = JsonParser.parseString(gson.toJson(resultAccessory));
    JsonElement expected = JsonParser.parseString(gson.toJson(expectedAccessory));
    Assert.assertEquals(result, expected);
  }

  @Test
  public void exceptionThrownIfQueryIsForAnAccessoryNotInTheDatabase()
      throws EntityNotFoundException {
    notInDatabaseExceptionRule.expect(EntityNotFoundException.class);
    Accessory resultAccessory = this.accessoryDatabase.getAccessory(ID_3);
  }
}
