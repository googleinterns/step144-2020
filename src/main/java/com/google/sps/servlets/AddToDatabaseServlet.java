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
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/addToDatabase")
/** Responds with a JSON string containing questions and answers for the career quiz */
public class AddToDatabaseServlet extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String CAREER_QUIZ_FORM_SUBMIT_PARAMETER = "careerQuizSubmit";
  private static final String CAREER_QUIZ_QUESTION_PARAMETER = "careerQuestion";
  private static final String CAREER_QUIZ_CHOICETEXT_PARAMETER = "careerChoiceText";
  private static final String CAREER_QUIZ_CAREERPATH_PARAMETER = "careerPath";
  private static final String CAREER_DATABASE_ENTITY_QUERY_STRING = "careerQuizQuestionAndChoices";
  private static final String CANT_SPLIT_STRING =
      "Choices and career paths need to be split by semicolons.";
  private static final String NUMBER_CHOICES_DONT_MATCH_CAREER_PATHS =
      "The number of choices given is not the same as the number of career paths given.";
      

  private static final Gson gson = new Gson();

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private ArrayList<QuizQuestion> quizQuestions;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getParameter(CAREER_QUIZ_FORM_SUBMIT_PARAMETER) != null) {
      addCareerQuizQuestion(request, response);
    }
  }

  private void addCareerQuizQuestion(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    QuestionDatabase careerQuestionDatabase = new QuestionDatabase(
        datastore, CAREER_DATABASE_ENTITY_QUERY_STRING);
    boolean validInput = true;
    String question = new String();
    List<String> choiceText = new ArrayList();
    List<String> careerPaths = new ArrayList();
    try {
      question = request.getParameter(CAREER_QUIZ_QUESTION_PARAMETER);
      choiceText = Arrays.asList(
          request.getParameter(CAREER_QUIZ_CHOICETEXT_PARAMETER).split(";"));
      careerPaths = Arrays.asList(
          request.getParameter(CAREER_QUIZ_CAREERPATH_PARAMETER).split(";"));
    } catch (PatternSyntaxException e) {
      validInput = false;
      handleInvalidInputError(CANT_SPLIT_STRING, response);
    }
    if (choiceText.size() != careerPaths.size()) {
      validInput = false;
      handleInvalidInputError(NUMBER_CHOICES_DONT_MATCH_CAREER_PATHS, response);
    }
    if (validInput) {
      ArrayList<QuestionChoice> choices = new ArrayList();
      for (int i = 0; i < choiceText.size(); i++) {
        QuestionChoice newChoice = new QuestionChoice(choiceText.get(i), careerPaths.get(i));
        choices.add(newChoice);
      }
      careerQuestionDatabase.putQuizQuestionsIntoDatabase(new QuizQuestion(question, choices));
    }
  }

  private void handleInvalidInputError(String message, HttpServletResponse response)
      throws IOException {
    // this message will be turned into an alert box
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(gson.toJson(message));
  }
}
