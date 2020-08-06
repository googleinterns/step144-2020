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
import java.util.List;
import java.util.Random;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Responds with a random accessory that the player does not already have */
@WebServlet("/earn-random-accessory")
public class EarnRandomAccessory extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String HTML_CONTENT_TYPE = "text/html";
  private static final String PLAYER_HAS_ALL_ACCESSORIES_ALREADY = "none";
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final String ENTITY_NOT_FOUND_EXCEPTION =
      "Earned id cannot be found in the database.";
  private static Gson gson;
  private Random randomGenerator;

  @Override
  public void init() {
    this.gson = new Gson();
    setRandomNumberGenerator(new Random());
  }

  public void setRandomNumberGenerator(Random randomGenerator) {
    this.randomGenerator = randomGenerator;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      UserService userService = UserServiceFactory.getUserService();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PlayerDatabase playerDatabase = new PlayerDatabase(datastore, userService);
      AccessoryDatabase accessoryDatabase = new AccessoryDatabase(datastore);
      List<String> playerAccessoryIDs = playerDatabase.getEntityAllAccessoryIDs();
      List<String> allAccessoryIDs = accessoryDatabase.getAllAccessoryIDs();
      allAccessoryIDs.removeAll(playerAccessoryIDs);
      if (allAccessoryIDs.size() == 0) {
        response.setContentType(JSON_CONTENT_TYPE);
        response.getWriter().println(gson.toJson(PLAYER_HAS_ALL_ACCESSORIES_ALREADY));
      } else {
        int index = randomGenerator.nextInt(allAccessoryIDs.size());
        String earnedAccessoryID = allAccessoryIDs.get(index);
        Accessory earnedAccessory = accessoryDatabase.getAccessory(earnedAccessoryID);
        playerDatabase.addToEntityAccessories(earnedAccessoryID);
        response.setContentType(JSON_CONTENT_TYPE);
        response.getWriter().println(gson.toJson(earnedAccessory));
      }
    } catch (LoggedOutException e) {
      response.setContentType(HTML_CONTENT_TYPE);
      response.getWriter().println(LOGGED_OUT_EXCEPTION);
      // EntityNotFoundException should never happen: the earnedAccessoryId originates from the
      // AccessoryDatabase, which maintains a list of the ids in the database.
    } catch (EntityNotFoundException e) {
      response.setContentType(HTML_CONTENT_TYPE);
      response.getWriter().println(ENTITY_NOT_FOUND_EXCEPTION);
    }
  }
}
