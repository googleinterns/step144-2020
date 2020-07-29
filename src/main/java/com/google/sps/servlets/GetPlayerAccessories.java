package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Accessory;
import com.google.sps.data.AccessoryDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Responds with a JSON string containing all accessory objects that the player has access to */
@WebServlet("/get-player-accessories")
public class GetPlayerAccessories extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String HTML_CONTENT_TYPE = "text/html";
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static Gson gson;
  private DatastoreService datastore;
  private UserService userService;
  private PlayerDatabase playerDatabase;
  private AccessoryDatabase accessoryDatabase;

  @Override
  public void init() {
    this.gson = new Gson();
    this.userService = UserServiceFactory.getUserService();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.playerDatabase = new PlayerDatabase(datastore, userService);
    this.accessoryDatabase = new AccessoryDatabase(datastore);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      List<String> accessoryIDs = this.playerDatabase.getEntityAllAccessoryIDs();
      List<Accessory> accessories = new ArrayList();
      for (String id : accessoryIDs) {
        try {
          accessories.add(this.accessoryDatabase.getAccessory(id));
        } catch (EntityNotFoundException e) {
          // if no entity is found, simply don't add to available accessories
          System.out.println(e.getMessage());
        }
      }
      String accessoriesJson = gson.toJson(accessories);
      response.setContentType(JSON_CONTENT_TYPE);
      response.getWriter().println(accessoriesJson);
    } catch (LoggedOutException e) {
      response.setContentType(HTML_CONTENT_TYPE);
      response.getWriter().println(LOGGED_OUT_EXCEPTION);
    }
  }
}
