package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.ProcessPromotionQuizResults;
import com.google.sps.data.PromotionMessage;
import com.google.sps.data.QuestionChoice;
import com.google.sps.data.QuestionDatabase;
import com.google.sps.data.QuizQuestion;
import java.io.IOException;
import java.util.ArrayList;
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
  private static final String HARDCODED_QUIZ_KEY = "softwareEngineerLevel1";
  private static final String PROMOTED_MESSAGE =
      "Congratulations, you passed the quiz and were promoted!";
  private static final String NOT_PROMOTED_MESSAGE =
      "You did not pass the quiz. Study the content and try again later";
  private static final Double CORRECT_NEEDED_THRESHOLD = 0.5;

  private static final Gson gson = new Gson();
  // future: threshold based on level https://github.com/googleinterns/step144-2020/issues/89

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private QuestionDatabase questionDatabase;
  private ArrayList<QuizQuestion> quizQuestions;

  @Override
  public void init() {
    String queryString = getGameStageSpecific_PromotionQuizQueryString();
    this.setQuestionDatabase(new QuestionDatabase(datastore, queryString));
  }

  public void setQuestionDatabase(QuestionDatabase questionDatabase) {
    this.questionDatabase = questionDatabase;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    this.quizQuestions = this.questionDatabase.getQuizQuestions();
    String quizQuestionsJson = gson.toJson(quizQuestions);
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(quizQuestionsJson);
  }

  /**
   * when submit button on promotion quiz is clicked, this method is called. if player does
   * sufficiently well on quiz, their gameStageId is progressed to next level. If not, they receive
   * a message and their gameStageId remains the same.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    this.quizQuestions = this.questionDatabase.getQuizQuestions();
    Boolean isPromoted = handleQuizSubmission(request);
    PromotionMessage promotionMessage =
        new PromotionMessage(isPromoted, isPromoted ? PROMOTED_MESSAGE : NOT_PROMOTED_MESSAGE);
    if (isPromoted) {
      // TODO: https://github.com/googleinterns/step144-2020/issues/88
      // then get the next id by calling currentGameStage.getNextStageId()
      // then set the players gameStage to the next id
    }
    String promotionJson = gson.toJson(promotionMessage);
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(promotionJson);
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

  public String getGameStageSpecific_PromotionQuizQueryString() {
    // TODO: https://github.com/googleinterns/step144-2020/pull/76
    // replace HARDCODED_GAME_STAGE_ID_STRING with:
    // creating a PlayerDatabase
    // calling getEntityCurrentPageID().getCurrentPageID();
    // (lookup page ID in game stage database and fetch appropriate game stage)
    // return gameStage.getQuizKey();
    return HARDCODED_QUIZ_KEY;
  }
}
