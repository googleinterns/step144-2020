package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;
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

/** Stores career question data in database */
@WebServlet("/addToDatabase")
public class StoreQuestion extends HttpServlet {
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
  private static final String REDIRECTION_URL = "admin/AddStaticData.html";
  private static final Gson gson = new Gson();
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getParameter(CAREER_QUIZ_FORM_SUBMIT_PARAMETER) != null) {
      addCareerQuizQuestion(request, response);
    }
  }

  private void addCareerQuizQuestion(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    QuestionDatabase careerQuestionDatabase =
        new QuestionDatabase(datastore, CAREER_DATABASE_ENTITY_QUERY_STRING);
    QuizQuestion quizQuestion = null;
    try {
      quizQuestion = getValidQuizQuestion(request);
      careerQuestionDatabase.putQuizQuestionsIntoDatabase(quizQuestion);
    } catch (PatternSyntaxException e) {
      handleInvalidInputError(CANT_SPLIT_STRING, response);
    } catch (IllegalArgumentException e) {
      handleInvalidInputError(e.getMessage(), response);
    }
    response.sendRedirect(REDIRECTION_URL);
  }

  private QuizQuestion getValidQuizQuestion(HttpServletRequest request)
      throws IllegalArgumentException, PatternSyntaxException {
    String question = new String();
    List<String> choiceText = new ArrayList();
    List<String> careerPaths = new ArrayList();
    question = request.getParameter(CAREER_QUIZ_QUESTION_PARAMETER);
    choiceText = Arrays.asList(request.getParameter(CAREER_QUIZ_CHOICETEXT_PARAMETER).split(";"));
    careerPaths = Arrays.asList(request.getParameter(CAREER_QUIZ_CAREERPATH_PARAMETER).split(";"));
    if (choiceText.size() != careerPaths.size()) {
      throw new IllegalArgumentException(NUMBER_CHOICES_DONT_MATCH_CAREER_PATHS);
    }
    ArrayList<QuestionChoice> choices = new ArrayList();
    for (int i = 0; i < choiceText.size(); i++) {
      QuestionChoice newChoice = new QuestionChoice(choiceText.get(i), careerPaths.get(i));
      choices.add(newChoice);
    }
    return new QuizQuestion(question, choices);
  }

  private void handleInvalidInputError(String message, HttpServletResponse response)
      throws IOException {
    // this message will be turned into an alert box
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(gson.toJson(message));
  }
}
