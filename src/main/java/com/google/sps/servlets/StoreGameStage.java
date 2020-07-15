package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Stores gameStage question data in database */
@WebServlet("/storeGameStage")
public class StoreGameStage extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String GAME_STAGE_FORM_SUBMIT_PARAMETER = "gameStageSubmit";
  private static final String GAME_STAGE_NAME_PARAMETER = "gameStageName";
  private static final String GAME_STAGE_CAREERPATH_PARAMETER = "gameStageCareerPath";
  private static final String GAME_STAGE_LEVEL_PARAMETER = "gameStageLevel";
  private static final String GAME_STAGE_CONTENT_PARAMETER = "gameStageContent";
  private static final String GAME_STAGE_IS_FINAL_PARAMETER = "gameStageIsFinal";
  private static final String YES_INPUT = "Yes";
  private static final String NO_INPUT = "No";
  private static final String REDIRECTION_URL = "admin/AddStaticData.html";
  private static Gson gson;
  private DatastoreService datastore;
  private GameStageDatabase gameStageDatabase;

  @Override
  public void init() {
    this.gson = new Gson();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.gameStageDatabase = new GameStageDatabase(datastore);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getParameter(GAME_STAGE_FORM_SUBMIT_PARAMETER) != null) {
      addGameStageToDatabase(request, response);
    }
  }

  private void addGameStageToDatabase(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    GameStage gameStage = getValidGameStage(request);
    this.gameStageDatabase.storeGameStage(gameStage);
    response.sendRedirect(REDIRECTION_URL);
  }

  private GameStage getValidGameStage(HttpServletRequest request) {
    String name = request.getParameter(GAME_STAGE_NAME_PARAMETER);
    String careerPath = request.getParameter(GAME_STAGE_CAREERPATH_PARAMETER);
    String level = request.getParameter(GAME_STAGE_LEVEL_PARAMETER);
    String content = request.getParameter(GAME_STAGE_CONTENT_PARAMETER);
    String finalStageString = request.getParameter(GAME_STAGE_IS_FINAL_PARAMETER);
    boolean isFinalStage = finalStageString.equals(YES_INPUT);
    String id = careerPath + level;
    String quizKey = id;
    Integer nextLevel = Integer.parseInt(level) + 1;
    String nextStageId = isFinalStage ? new String() : careerPath + nextLevel.toString();
    return new GameStage(name, content, id, quizKey, isFinalStage, nextStageId);
  }

  private void handleInvalidInputError(String message, HttpServletResponse response)
      throws IOException {
    // this message will be turned into an alert box
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(gson.toJson(message));
  }
}
