package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Accessory;
import com.google.sps.data.Accessory.Type;
import com.google.sps.data.AccessoryDatabase;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Stores Accessory data in database */
@WebServlet("/store-accessory")
public class StoreAccessory extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String SUBMIT_PARAMETER = "accessoryFormSubmit";
  private static final String FILE_PARAMETER = "filepath";
  private static final String TYPE_PARAMETER = "type";
  private static final String HEIGHT_PARAMETER = "height";
  private static final String WIDTH_PARAMETER = "width";
  private static final String XPOS_PARAMETER = "xPos";
  private static final String YPOS_PARAMETER = "yPos";
  private static final String REDIRECTION_URL = "admin/StoreAccessory.html";
  private static final String FILEPATH_ROOT = "images/accessories/";
  private static Gson gson;
  private File file;
  private DatastoreService datastore;
  private AccessoryDatabase accessoryDatabase;

  @Override
  public void init() {
    this.gson = new Gson();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.accessoryDatabase = new AccessoryDatabase(datastore);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: refactor to a CheckIfSubmitButtonClicked method in helper class
    // https://github.com/googleinterns/step144-2020/issues/194
    if (request.getParameter(SUBMIT_PARAMETER) != null) {
      addAccessoryToDatabase(request, response);
    }
  }

  private void addAccessoryToDatabase(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    Accessory accessory = getValidAccessory(request);
    this.accessoryDatabase.storeAccessory(accessory);
    response.sendRedirect(REDIRECTION_URL);
  }

  private Accessory getValidAccessory(HttpServletRequest request) throws FileNotFoundException {
    // no checks on whether or not file exists as that is shown on the front end form
    String filepath = FILEPATH_ROOT + request.getParameter(FILE_PARAMETER);
    // constrained to give valid type because of radio button/mult choice selection
    String typeString = request.getParameter(TYPE_PARAMETER);
    Type type = Type.valueOf(typeString);
    // following are restricted to int types by the html form input constrains
    int height = Integer.parseInt(request.getParameter(HEIGHT_PARAMETER));
    int width = Integer.parseInt(request.getParameter(WIDTH_PARAMETER));
    int xPos = Integer.parseInt(request.getParameter(XPOS_PARAMETER));
    int yPos = Integer.parseInt(request.getParameter(YPOS_PARAMETER));
    String id = filepath + typeString;
    return new Accessory(id, filepath, type, height, width, xPos, yPos);
  }
}
