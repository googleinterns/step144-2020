package com.google.sps;

import static org.mockito.Mockito.when;

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
import com.google.sps.servlets.StoreAccessory;
import java.io.IOException;
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

/** Tests that the StoreAccessory correctly adds static entities to the game datastore */
@RunWith(JUnit4.class)
public final class StoreAccessoryTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private StoreAccessory storeAccessory;
  private AccessoryDatabase accessoryDatabase;
  private static final String SUBMIT_PARAMETER = "accessoryFormSubmit";
  private static final String FILE_PARAMETER = "filepath";
  private static final String TYPE_PARAMETER = "type";
  private static final String HEIGHT_PARAMETER = "height";
  private static final String WIDTH_PARAMETER = "width";
  private static final String XPOS_PARAMETER = "xPos";
  private static final String YPOS_PARAMETER = "yPos";
  private static final String IMAGES_FOLDER = "images";
  private static final String ACCESSORIES_FOLDER = "accessories";
  private static final String FILEPATH_1 = "filepath1.png";
  private static final String FILEPATH_2 = "filepath2.png";
  private static final String HAT = "HAT";
  private static final String GLASSES = "GLASSES";
  private static final String COMPANION = "COMPANION";
  private static final String PIXEL_5 = "5";
  private static final String PIXEL_20 = "20";
  private static final String PIXEL_40 = "40";
  private static final String PIXEL_100 = "100";
  private static final boolean SUBMIT_CLICKED = true;
  private static final boolean SUBMIT_NOT_CLICKED = false;
  private static final Gson gson = new Gson();

  @Rule public ExpectedException entityNotFoundExceptionRule = ExpectedException.none();

  @Before
  public void setUp() throws IOException {
    helper.setUp(); // initialize local datastore for testing
    DatastoreService localDatastore = DatastoreServiceFactory.getDatastoreService();
    this.accessoryDatabase = new AccessoryDatabase(localDatastore);
    MockitoAnnotations.initMocks(this);
    this.storeAccessory = new StoreAccessory();
    this.storeAccessory.init();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests that the doPost method fetches form parameters, creates appropriate Accessory */
  @Test
  public void doPost_withValidInput_addsToDatabase() throws IOException, EntityNotFoundException {
    // mocks user entering in accessory data
    boolean submitInput = SUBMIT_CLICKED;
    String filepathInput = FILEPATH_1;
    String typeStringInput = GLASSES;
    String heightInput = PIXEL_100;
    String widthInput = PIXEL_20;
    String xPosInput = PIXEL_40;
    String yPosInput = PIXEL_20;

    mockUserAccessoryInput(
        submitInput, filepathInput, typeStringInput, heightInput, widthInput, xPosInput, yPosInput);

    String filepath = IMAGES_FOLDER + "/" + ACCESSORIES_FOLDER + "/" + FILEPATH_1;
    Type type = Type.GLASSES;
    int height = 100;
    int width = 20;
    int xPos = 40;
    int yPos = 20;
    String id = filepath + Type.GLASSES.name();

    Accessory expectedAccessory = new Accessory(id, filepath, type, height, width, xPos, yPos);

    this.storeAccessory.doPost(this.request, this.response);
    Accessory resultAccessory = this.accessoryDatabase.getAccessory(filepath + typeStringInput);

    JsonElement result = JsonParser.parseString(gson.toJson(resultAccessory));
    JsonElement expected = JsonParser.parseString(gson.toJson(expectedAccessory));
    Assert.assertEquals(result, expected);
  }

  /** Tests that nothing is added to database if submit is not clicked, exception thrown */
  @Test
  public void doPost_submitNotClicked_nothingAddedToDatabase()
      throws IOException, EntityNotFoundException {
    // mocks user entering in accessory data
    boolean submitInput = SUBMIT_NOT_CLICKED;
    String filepathInput = FILEPATH_1;
    String typeStringInput = GLASSES;
    String heightInput = PIXEL_100;
    String widthInput = PIXEL_20;
    String xPosInput = PIXEL_40;
    String yPosInput = PIXEL_20;

    mockUserAccessoryInput(
        submitInput, filepathInput, typeStringInput, heightInput, widthInput, xPosInput, yPosInput);

    String filepath = IMAGES_FOLDER + "/" + ACCESSORIES_FOLDER + "/" + FILEPATH_1;
    Type type = Type.GLASSES;
    int height = 100;
    int width = 20;
    int xPos = 40;
    int yPos = 20;
    String id = filepath + Type.GLASSES.name();

    Accessory expectedAccessory = new Accessory(id, filepath, type, height, width, xPos, yPos);

    this.storeAccessory.doPost(this.request, this.response);

    entityNotFoundExceptionRule.expect(EntityNotFoundException.class);
    Accessory resultAccessory = this.accessoryDatabase.getAccessory(filepath + typeStringInput);
  }

  private void mockUserAccessoryInput(
      boolean submitClicked,
      String filepathInput,
      String typeStringInput,
      String heightInput,
      String widthInput,
      String xPosInput,
      String yPosInput) {
    String submitReturn = submitClicked ? SUBMIT_PARAMETER : null;
    when(this.request.getParameter(SUBMIT_PARAMETER)).thenReturn(submitReturn);
    when(this.request.getParameter(FILE_PARAMETER)).thenReturn(filepathInput);
    when(this.request.getParameter(TYPE_PARAMETER)).thenReturn(typeStringInput);
    when(this.request.getParameter(HEIGHT_PARAMETER)).thenReturn(heightInput);
    when(this.request.getParameter(WIDTH_PARAMETER)).thenReturn(widthInput);
    when(this.request.getParameter(XPOS_PARAMETER)).thenReturn(xPosInput);
    when(this.request.getParameter(YPOS_PARAMETER)).thenReturn(yPosInput);
  }
}
