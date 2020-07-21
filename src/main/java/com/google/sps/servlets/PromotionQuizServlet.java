package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.PlayerDatabase;
import com.google.sps.data.ProcessPromotionQuizResults;
import com.google.sps.data.PromotionMessage;
import com.google.sps.data.QuestionChoice;
import com.google.sps.data.QuestionDatabase;
import com.google.sps.data.QuizQuestion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/promotionQuiz")
/** Responds with a JSON string containing questions and answers for the promotion quiz */
public class PromotionQuizServlet extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String QUIZ_SUBMIT = "promotionQuizSubmit";
  // this will be replaced by the game stage id queried from the user
  private static final String PROMOTED_MESSAGE =
      "Congratulations, you passed the quiz and were promoted!";
  private static final String NOT_PROMOTED_MESSAGE =
      "You did not pass the quiz. Study the content and try again later";
  private static final String IS_FINAL_STAGE_MESSAGE =
      "Congratulations! You reached the final stage! You may no longer be promoted in this path.";
  // future: threshold based on level https://github.com/googleinterns/step144-2020/issues/89
  private static final Double CORRECT_NEEDED_THRESHOLD = 0.5;
  private static Gson gson;
  private DatastoreService datastore;
  private UserService userService;
  private GameStageDatabase gameStageDatabase;
  private PlayerDatabase playerDatabase;
  private List<QuizQuestion> quizQuestions;

  @Override
  public void init() {
    this.gson = new Gson();
    this.userService = UserServiceFactory.getUserService();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.gameStageDatabase = new GameStageDatabase(datastore);
    this.playerDatabase = new PlayerDatabase(datastore, userService);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(JSON_CONTENT_TYPE);
    try {
      if (isUserOnFinalStage()) {
        response.getWriter().println(gson.toJson(IS_FINAL_STAGE_MESSAGE));
      } else {
        this.quizQuestions = getQuizQuestions(response);
        String quizQuestionsJson = gson.toJson(quizQuestions);
        response.getWriter().println(quizQuestionsJson);
      }
    } catch (LoggedOutException e) {
      handleNotLoggedInUser(e.getMessage(), response);
    }
  }

  /**
   * when submit button on promotion quiz is clicked, this method is called. if player does
   * sufficiently well on quiz, their gameStageId is progressed to next level. If not, they receive
   * a message and their gameStageId remains the same.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      this.quizQuestions = getQuizQuestions(response);
      Boolean isPromoted = handleQuizSubmission(request);
      PromotionMessage promotionMessage =
          new PromotionMessage(isPromoted, isPromoted ? PROMOTED_MESSAGE : NOT_PROMOTED_MESSAGE);
      if (isPromoted) {
        String nextGameStageId = getCurrentGameStage().getNextStageID();
        this.playerDatabase.setEntityCurrentPageID(nextGameStageId);
      }
      String promotionJson = gson.toJson(promotionMessage);
      response.getWriter().println(promotionJson);
    } catch (LoggedOutException e) {
      handleNotLoggedInUser(e.getMessage(), response);
    }
  }

  private GameStage getCurrentGameStage() throws LoggedOutException {
    String currentGameStageId = this.playerDatabase.getEntityCurrentPageID();
    return this.gameStageDatabase.getGameStage(currentGameStageId);
  }

  private boolean isUserOnFinalStage() throws LoggedOutException {
    GameStage currentGameStage = getCurrentGameStage();
    return currentGameStage.isFinalStage();
  }

  private List<QuizQuestion> getQuizQuestions(HttpServletResponse response) throws IOException {
    String queryString = new String();
    try {
      queryString = getCurrentGameStage().getQuizKey();
    } catch (LoggedOutException e) {
      handleNotLoggedInUser(e.getMessage(), response);
    }
    QuestionDatabase questionDatabase = new QuestionDatabase(this.datastore, queryString);
    return questionDatabase.getQuizQuestions();
  }

  private Boolean handleQuizSubmission(HttpServletRequest request) {
    ArrayList<QuestionChoice> userChoices = new ArrayList();
    for (QuizQuestion quizQuestion : this.quizQuestions) {
      String selectedChoice = request.getParameter(quizQuestion.getQuestion());
      QuestionChoice userChoice = gson.fromJson(selectedChoice, QuestionChoice.class);
      userChoices.add(userChoice);
    }
    Boolean isPromoted =
        ProcessPromotionQuizResults.isPromoted(userChoices, CORRECT_NEEDED_THRESHOLD);
    return isPromoted;
  }

  private void handleNotLoggedInUser(String message, HttpServletResponse response)
      throws IOException {
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(gson.toJson(message));
  }
}
