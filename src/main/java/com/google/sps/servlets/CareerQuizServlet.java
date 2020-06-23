package com.google.sps.servlets;

import com.google.sps.data.CareerQuestionDatabase;
import com.google.sps.data.CareerQuestionAndChoices;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


@WebServlet("/careerquiz")
public class CareerQuizServlet extends HttpServlet {
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final CareerQuestionDatabase careerQuestionDatabase = new CareerQuestionDatabase(this.datastore);
  private static final String JSON_CONTENT_TYPE = "application/json;";

  /* responds with a JSON string containing questions and answers for the career quiz*/
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<CareerQuestionAndChoices> questionsAndChoices = 
        this.careerQuestionDatabase.getQuestionsAndChoices();
    // Convert the ArrayLists to JSON
    Gson gson = new Gson();
    String questionsAndChoicesJson = gson.toJson(questionsAndChoices);
    // Send the JSON as the response
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(questionsAndChoicesJson);
  }
}
