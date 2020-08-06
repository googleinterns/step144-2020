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
 * Servlet responsible for retreving and updating users' experience points. Experience Points are
 * accumulated by players as they play the game. The amount of points a player has determines when
 * they can try for promotion, when a special event may occur, or whenthe player recieves a reward.
 * They are a scoring metric.
 */
@WebServlet("/experience")
public class ExperienceServlet extends HttpServlet {
  private static final String CONTENT_TYPE = "text/html";
  private static final String EXPERIENCE_PARAMETER = "experiencePoints";
  private static final String EMPTY_PARAMETER = "empty";
  private static Gson gson = new Gson();
  private static int experience;
  private DatastoreService datastore;
  private UserService userService;
  private PlayerDatabase playerDatabase;
  private boolean isLoggedIn;

  private void updateService() throws LoggedOutException {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.userService = UserServiceFactory.getUserService();
    this.playerDatabase = new PlayerDatabase(datastore, userService);
    this.isLoggedIn = userService.isUserLoggedIn();
  }

  // Update the player's experience points
  // Experience Points are the scoring metric of the game.
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      updateService();
      experience = Integer.parseInt(request.getParameter(EXPERIENCE_PARAMETER).toString());
      this.playerDatabase.setEntityExperience(experience);
    } catch (LoggedOutException e) {
      response.setContentType(CONTENT_TYPE);
      response.getWriter().println(e.getMessage());
    } catch (NumberFormatException e) {
      response.setContentType(CONTENT_TYPE);
      response.getWriter().println(e.getMessage());
    }
  }

  // Retrieve the player's experience points from the player database.
  // Experience Points are the scoring metric of the game.
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      updateService();
      handleLoggedInUser(response);
    } catch (LoggedOutException e) {
      handleLoggedOutUser(response);
    }
  }

  private void handleLoggedInUser(HttpServletResponse response) throws IOException {
    response.getWriter();
    response.setContentType(CONTENT_TYPE);
    try {
      experience = this.playerDatabase.getEntityExperience();
    } catch (LoggedOutException e) {
      response.getWriter().println(e.getMessage());
    }
    response.getWriter().println(experience);
  }

  private void handleLoggedOutUser(HttpServletResponse response) throws IOException {
    response.getWriter().println(EMPTY_PARAMETER);
  }
}
