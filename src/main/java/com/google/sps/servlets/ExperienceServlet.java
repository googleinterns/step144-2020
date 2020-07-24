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

/** Allows users to start new game by deleting their previous progress/info */
@WebServlet("/experience")
public class ExperienceServlet extends HttpServlet {
  private static final String CONTENT_TYPE = "text/html";
  private static final String EXPERIENCE_PARAMETER = "experience";
  private static final String EMPTY_PARAMETER = "empty";
  private static Gson gson;
  private static int experience;
  private DatastoreService datastore;
  private UserService userService;
  private PlayerDatabase playerDatabase;
  private boolean isLoggedIn;

  @Override
  public void init() {
    this.gson = new Gson();
    this.userService = UserServiceFactory.getUserService();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.playerDatabase = new PlayerDatabase(datastore, userService);
    this.isLoggedIn = userService.isUserLoggedIn();
  }

  // Update the player's experience points
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    experience = Integer.parseInt(request.getParameter(EXPERIENCE_PARAMETER).toString());
    try {
      playerDatabase.setEntityExperience(experience);
    } catch (LoggedOutException e) {
    }
  }

  // Retrieve the player's experience points
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (isLoggedIn) {
      handleLoggedInUser(response);
    } else {
      handleLoggedOutUser(response);
    }
  }

  private void handleLoggedInUser(HttpServletResponse response) throws IOException {
    response.getWriter();
    response.setContentType(CONTENT_TYPE);
    try {
      experience = playerDatabase.getEntityExperience();
    } catch (LoggedOutException e) {
    }
    response.getWriter().println(experience);
  }

  private void handleLoggedOutUser(HttpServletResponse response) throws IOException {
    response.getWriter().println(EMPTY_PARAMETER);
  }
}
