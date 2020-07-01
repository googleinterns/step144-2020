package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.CareerQuestionAndChoices;
import com.google.sps.data.CareerQuestionChoice;
import com.google.sps.data.CareerQuestionDatabase;
import com.google.sps.data.ProcessCareerQuizResults;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

@WebServlet("/careerquiz")
/** Responds with a JSON string containing questions and answers for the career quiz */
public class CareerQuizServlet extends HttpServlet {
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final CareerQuestionDatabase careerQuestionDatabase =
      new CareerQuestionDatabase(datastore);
  private ArrayList<CareerQuestionAndChoices> questionsAndChoices;
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String QUIZ_SUBMIT = "career-quiz-submit";
  private static final Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    this.questionsAndChoices = this.careerQuestionDatabase.getQuestionsAndChoices();
    // Convert the ArrayLists to JSON
    Gson gson = new Gson();
    String questionsAndChoicesJson = gson.toJson(questionsAndChoices);
    // Send the career questions and choices JSON as the response
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(questionsAndChoicesJson);
  }

  /**
   * when submit button on career quiz is clicked, this method is called and returns recommended
   * path based on selected choices
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    this.questionsAndChoices = this.careerQuestionDatabase.getQuestionsAndChoices();
    String recommendedPath = handleQuizSubmission(request);
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(gson.toJson(recommendedPath));
  }

  private String handleQuizSubmission(HttpServletRequest request) {
    ArrayList<CareerQuestionChoice> userChoices = new ArrayList();
    for (CareerQuestionAndChoices questionAndChoice : this.questionsAndChoices) {
      String selectedChoice = request.getParameter(questionAndChoice.getQuestion());
      CareerQuestionChoice userChoice = gson.fromJson(selectedChoice, CareerQuestionChoice.class);
      userChoices.add(userChoice);
    }
    String recommendedPath = ProcessCareerQuizResults.getRecommendedCareerPath(userChoices);
    return recommendedPath;
  }
}
