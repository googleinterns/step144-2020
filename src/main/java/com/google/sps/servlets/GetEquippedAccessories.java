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
  private DatastoreService datastore;
  private UserService userService;
  private PlayerDatabase playerDatabase;
  private AccessoryDatabase accessoryDatabase;

  @Override
  public void init() {
    this.gson = new Gson();
    this.jsonParser = new JsonParser();
    this.userService = UserServiceFactory.getUserService();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.playerDatabase = new PlayerDatabase(datastore, userService);
    this.accessoryDatabase = new AccessoryDatabase(datastore);
  }

  /** Responds with a Json message that includes all equipped accessories */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      String equippedHatID = this.playerDatabase.getEntityEquippedHatID();
      String equippedGlassesID = this.playerDatabase.getEntityEquippedGlassesID();
      String equippedCompanionID = this.playerDatabase.getEntityEquippedCompanionID();
      JsonObject accessoryJson =
          createJsonEquippedAccessoryObject(equippedHatID, equippedGlassesID, equippedCompanionID);
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
      String equippedHatID, String equippedGlassesID, String equippedCompanionID) {
    JsonObject accessoryJson = new JsonObject();

    accessoryJson.add(EQUIPPED_HAT, gson.toJsonTree(getJsonTreeFromID(equippedHatID)));
    accessoryJson.add(EQUIPPED_GLASSES, gson.toJsonTree(getJsonTreeFromID(equippedGlassesID)));
    accessoryJson.add(EQUIPPED_COMPANION, gson.toJsonTree(getJsonTreeFromID(equippedCompanionID)));
    return accessoryJson;
  }

  /** if the player does not have the accessory equipped/ it is not found, returns NONE_EQUIPPED */
  private JsonElement getJsonTreeFromID(String id) {
    String noneEquippedJson = gson.toJson(NONE_EQUIPPED);
    JsonElement noneEquippedJsonElement = jsonParser.parse(noneEquippedJson);
    try {
      return id == NONE_EQUIPPED
          ? noneEquippedJsonElement
          : gson.toJsonTree(this.accessoryDatabase.getAccessory(id));
    } catch (EntityNotFoundException e) {
      return noneEquippedJsonElement;
    }
  }
}
