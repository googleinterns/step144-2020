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

/** Sets the users game stage when they click on their initial branch */
@WebServlet("/initializeGameStage")
public class InitializeGameStage extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String FORM_SUBMIT_PARAMETER = "pathSubmit";
  private static final String REDIRECTION_URL = "gameStage.html";
  private static final String LEVEL_1 = "1";
  private static Gson gson = new Gson();
  DatastoreService datastore;
  UserService userService;
  PlayerDatabase playerDatabase;

  private void updateService() throws LoggedOutException {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.userService = UserServiceFactory.getUserService();
    this.playerDatabase = new PlayerDatabase(datastore, userService);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      updateService();
      if (request.getParameter(FORM_SUBMIT_PARAMETER) != null) {
        setUserGameStage(request, response);
      }
    } catch (LoggedOutException e) {
      handleNotLoggedInUser(e.getMessage(), response);
    }
  }

  private void setUserGameStage(HttpServletRequest request, HttpServletResponse response)
      throws IOException, LoggedOutException {
    String careerPath = request.getParameter(FORM_SUBMIT_PARAMETER);
    String gameStageID = careerPath + LEVEL_1;
    this.playerDatabase.setEntityCurrentPageID(gameStageID);
    response.sendRedirect(REDIRECTION_URL);
  }

  private void handleNotLoggedInUser(String message, HttpServletResponse response)
      throws IOException {
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(gson.toJson(message));
  }
}
