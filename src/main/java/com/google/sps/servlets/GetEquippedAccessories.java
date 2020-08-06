package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sps.data.AccessoryDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/customization")
public class GetEquippedAccessories extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String HTML_CONTENT_TYPE = "text/html";
  private static final String EQUIPPED_HAT = "equippedHat";
  private static final String EQUIPPED_GLASSES = "equippedGlasses";
  private static final String EQUIPPED_COMPANION = "equippedCompanion";
  private static final String NONE_EQUIPPED = "noneEquipped";
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String NOT_FOUND_EXCEPTION =
      "Something went wrong. The accessory was not found in the database.";
  private static Gson gson;
  private static JsonParser jsonParser;

  @Override
  public void init() {
    this.gson = new Gson();
    this.jsonParser = new JsonParser();
  }

  /** Responds with a Json message that includes all equipped accessories */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      UserService userService = UserServiceFactory.getUserService();
      PlayerDatabase playerDatabase = new PlayerDatabase(datastore, userService);
      AccessoryDatabase accessoryDatabase = new AccessoryDatabase(datastore);
      
      String equippedHatID = playerDatabase.getEntityEquippedHatID();
      String equippedGlassesID = playerDatabase.getEntityEquippedGlassesID();
      String equippedCompanionID = playerDatabase.getEntityEquippedCompanionID();
      JsonObject accessoryJson =
          createJsonEquippedAccessoryObject(
              accessoryDatabase, equippedHatID, equippedGlassesID, equippedCompanionID);
      String accessoryMessage = accessoryJson.toString();
      response.setContentType(JSON_CONTENT_TYPE);
      response.getWriter().println(accessoryMessage);
    } catch (LoggedOutException e) {
      response.setContentType(HTML_CONTENT_TYPE);
      response.getWriter().println(LOGGED_OUT_EXCEPTION);
    }
  }

  /** Creates JsonObject that encapsulates all equipped accessories */
  private JsonObject createJsonEquippedAccessoryObject(
      AccessoryDatabase accessoryDatabase,
      String equippedHatID,
      String equippedGlassesID,
      String equippedCompanionID) {
    JsonObject accessoryJson = new JsonObject();

    accessoryJson.add(
        EQUIPPED_HAT, gson.toJsonTree(getJsonTreeFromID(accessoryDatabase, equippedHatID)));
    accessoryJson.add(
        EQUIPPED_GLASSES, gson.toJsonTree(getJsonTreeFromID(accessoryDatabase, equippedGlassesID)));
    accessoryJson.add(
        EQUIPPED_COMPANION, gson.toJsonTree(getJsonTreeFromID(accessoryDatabase, equippedCompanionID)));
    return accessoryJson;
  }

  /** if the player does not have the accessory equipped/ it is not found, returns NONE_EQUIPPED */
  private JsonElement getJsonTreeFromID(AccessoryDatabase accessoryDatabase, String id) {
    String noneEquippedJson = gson.toJson(NONE_EQUIPPED);
    JsonElement noneEquippedJsonElement = jsonParser.parse(noneEquippedJson);
    try {
      return id == NONE_EQUIPPED
          ? noneEquippedJsonElement
          : gson.toJsonTree(accessoryDatabase.getAccessory(id));
    } catch (EntityNotFoundException e) {
      return noneEquippedJsonElement;
    }
  }

  /** Sets the players equipped accessories */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      UserService userService = UserServiceFactory.getUserService();
      PlayerDatabase playerDatabase = new PlayerDatabase(datastore, userService);
      AccessoryDatabase accessoryDatabase = new AccessoryDatabase(datastore);

      String equippedHatID = request.getParameter(EQUIPPED_HAT);
      String equippedGlassesID = request.getParameter(EQUIPPED_GLASSES);
      String equippedCompanionID = request.getParameter(EQUIPPED_COMPANION);

      playerDatabase.setEntityEquippedHatID(defaultIfNotFound(accessoryDatabase, equippedHatID));
      playerDatabase.setEntityEquippedGlassesID(defaultIfNotFound(accessoryDatabase, equippedGlassesID));
      playerDatabase.setEntityEquippedCompanionID(defaultIfNotFound(accessoryDatabase, equippedCompanionID));
    } catch (LoggedOutException e) {
      response.setContentType(HTML_CONTENT_TYPE);
      response.getWriter().println(LOGGED_OUT_EXCEPTION);
    }
  }

  /* Safeguard function to ensure that a player always has either a valid accessory id set or
   * the NONE_EQUIPPED string (which is used in this servlets doGet method to determine whether
   * or not to send an AccessoryObject or just a NONE_EQUIPPED message.
   */
  private String defaultIfNotFound(AccessoryDatabase accessoryDatabase, String accessoryId) {
    // this if statement will trigger the exception and the return of NONE_EQUIPPED either
    // way, but this check is put here to save time
    if (accessoryId == null || accessoryId.equals(NONE_EQUIPPED)) {
      return NONE_EQUIPPED;
    }

    try {
      accessoryDatabase.getAccessory(accessoryId);
      return accessoryId;
    } catch (EntityNotFoundException e) {
      return NONE_EQUIPPED;
    }
  }
}
