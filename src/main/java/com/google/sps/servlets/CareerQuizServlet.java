package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.ProcessCareerQuizResults;
import com.google.sps.data.QuestionChoice;
import com.google.sps.data.QuestionDatabase;
import com.google.sps.data.QuizQuestion;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/careerquiz")
/** Responds with a JSON string containing questions and answers for the career quiz */
public class CareerQuizServlet extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String QUIZ_SUBMIT = "career-quiz-submit";
  private static final String ENTITY_QUERY_STRING = "careerQuizQuestionAndChoices";
  private static final Gson gson = new Gson();

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final QuestionDatabase questionDatabase =
      new QuestionDatabase(datastore, ENTITY_QUERY_STRING);
  private ArrayList<QuizQuestion> quizQuestions;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    this.quizQuestions = this.questionDatabase.getQuizQuestions();
    // Convert the ArrayLists to JSON
    Gson gson = new Gson();
    String quizQuestionsJson = gson.toJson(quizQuestions);
    // Send the career questions and choices JSON as the response
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(quizQuestionsJson);
  }

  /**
   * when submit button on career quiz is clicked, this method is called and returns recommended
   * path based on selected choices
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    this.quizQuestions = this.questionDatabase.getQuizQuestions();
    String recommendedPath = handleQuizSubmission(request);
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(gson.toJson(recommendedPath));
  }

  private String handleQuizSubmission(HttpServletRequest request) {
    ArrayList<QuestionChoice> userChoices = new ArrayList();
    for (QuizQuestion quizQuestion : this.quizQuestions) {
      String selectedChoice = request.getParameter(quizQuestion.getQuestion());
      QuestionChoice userChoice = gson.fromJson(selectedChoice, QuestionChoice.class);
      userChoices.add(userChoice);
    }
    String recommendedPath = ProcessCareerQuizResults.getRecommendedCareerPath(userChoices);
    return recommendedPath;
  }
}
