package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for retreiving and updating users' promotion threshold. Experience Points are
 * accumulated by players as they play the game. The amount of experience points a player has
 * determines when they can try for promotion, when a special event may occur, or when the player
 * recieves a reward. The number of experience points a user needs to be able to try for promotion
 * changes as the player earns more points, so the threshold is stored with the player, and needs to
 * be fetched and updated regularly.
 */
@WebServlet("/promotion-threshold")
public class PromotionThresholdServlet extends HttpServlet {
  private static final String CONTENT_TYPE = "text/html";
  private static final String PROMOTION_THRESHOLD_PARAMETER = "promotionThreshold";
  private static final String NOT_LOGGED_IN_PARAMETER = "User Not Logged In";
  private static Gson gson = new Gson();
  private static int promotionThreshold;

  // Update the player's promotion threshold
  // The promotion threshold is how many experience points a player needs to try for promotion.
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      UserService userService = UserServiceFactory.getUserService();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PlayerDatabase playerDatabase = new PlayerDatabase(datastore, userService);
      boolean isLoggedIn = userService.isUserLoggedIn();
      promotionThreshold =
          Integer.parseInt(request.getParameter(PROMOTION_THRESHOLD_PARAMETER).toString());
      playerDatabase.setEntityPromotionThreshold(promotionThreshold);
    } catch (LoggedOutException e) {
      response.setContentType(CONTENT_TYPE);
      response.getWriter().println(e.getMessage());
    } catch (NumberFormatException e) {
      response.setContentType(CONTENT_TYPE);
      response.getWriter().println(e.getMessage());
    }
  }

  // Retrieve the player's promotion threshold from the player database.
  // The promotion threshold is how many experience points a player needs to try for promotion.
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    boolean isLoggedIn = userService.isUserLoggedIn();
    if (isLoggedIn) {
      handleLoggedInUser(userService, datastore, response);
    } else {
      handleLoggedOutUser(response);
    }
  }

  private void handleLoggedInUser(
      UserService userService, DatastoreService datastore, HttpServletResponse response)
      throws IOException {
    try {
      PlayerDatabase playerDatabase = new PlayerDatabase(datastore, userService);
      response.getWriter();
      response.setContentType(CONTENT_TYPE);
      promotionThreshold = playerDatabase.getEntityPromotionThreshold();
      response.getWriter().println(promotionThreshold);
    } catch (LoggedOutException e) {
      response.getWriter().println(e.getMessage());
    }
  }

  private void handleLoggedOutUser(HttpServletResponse response) throws IOException {
    response.getWriter().println(NOT_LOGGED_IN_PARAMETER);
  }
}
