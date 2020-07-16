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
@WebServlet("/storePromotion")
public class StorePromotionQuestion extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String FORM_SUBMIT_PARAMETER = "promotionQuizSubmit";
  private static final String QUESTION_PARAMETER = "promotionQuestion";
  private static final String CHOICE_TEXT_PARAMETER = "promotionChoiceText";
  private static final String CHOICE_IS_ACCEPTED_PARAMETER = "promotionAcceptedChoice";
  private static final String QUIZ_CAREERPATH_PARAMETER = "promotionCareerPath";
  private static final String QUIZ_LEVEL_PARAMETER = "promotionQuizLevel";
  private static final String ACCEPTED_CHOICE = "y";
  private static final String NOT_ACCEPTED_CHOICE = "n";
  private static final String CANNOT_SPLIT_STRING =
      "Choices and whether they are accepted need to be split by semicolons.";
  private static final String NUMBER_CHOICES_DO_NOT_MATCH_ACCEPTED_SPEC =
      "The specification of the choice options of being accepted or not is the same length as the "
          + "number of choices.";
  private static final String IS_ACCEPTED_INCORRECT_FORMAT =
      "The specification of the choice options of being accepted or not is not given by y or n.";
  private static final String REDIRECTION_URL = "admin/AddStaticData.html";
  private static Gson gson;
  private DatastoreService datastore;

  @Override
  public void init() {
    this.gson = new Gson();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getParameter(FORM_SUBMIT_PARAMETER) != null) {
      addPromotionQuizQuestion(request, response);
    }
  }

  private void addPromotionQuizQuestion(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String quizQueryString = getQuizQueryString(request);
    QuestionDatabase quizQuestionDatabase = new QuestionDatabase(datastore, quizQueryString);
    QuizQuestion quizQuestion = null;
    try {
      quizQuestion = getValidQuizQuestion(request);
      quizQuestionDatabase.putQuizQuestionsIntoDatabase(quizQuestion);
    } catch (PatternSyntaxException e) {
      handleInvalidInputError(CANNOT_SPLIT_STRING, response);
    } catch (IllegalArgumentException e) {
      handleInvalidInputError(e.getMessage(), response);
    }
    response.sendRedirect(REDIRECTION_URL);
  }

  private String getQuizQueryString(HttpServletRequest request) {
    String quizCareerPath = request.getParameter(QUIZ_CAREERPATH_PARAMETER);
    String quizLevel = request.getParameter(QUIZ_LEVEL_PARAMETER);
    return (quizCareerPath + quizLevel);
  }

  private QuizQuestion getValidQuizQuestion(HttpServletRequest request)
      throws IllegalArgumentException, PatternSyntaxException {

    String question = request.getParameter(QUESTION_PARAMETER);
    List<String> choiceText = Arrays.asList(request.getParameter(CHOICE_TEXT_PARAMETER).split(";"));
    List<String> isAcceptedStrings =
        Arrays.asList(request.getParameter(CHOICE_IS_ACCEPTED_PARAMETER).split(";"));
    String quizCareerPath = request.getParameter(QUIZ_CAREERPATH_PARAMETER);

    List<Boolean> isAccepted = new ArrayList();
    for (String isAcceptedString : isAcceptedStrings) {
      // strip whitespace, make lower case
      isAcceptedString = isAcceptedString.replaceAll("\\s+", "").toLowerCase();
      if (isAcceptedString.equals(ACCEPTED_CHOICE)) {
        isAccepted.add(true);
      } else if (isAcceptedString.equals(NOT_ACCEPTED_CHOICE)) {
        isAccepted.add(false);
      } else {
        throw new IllegalArgumentException(IS_ACCEPTED_INCORRECT_FORMAT);
      }
    }
    if (!(choiceText.size() == isAccepted.size())) {
      throw new IllegalArgumentException(NUMBER_CHOICES_DO_NOT_MATCH_ACCEPTED_SPEC);
    }
    ArrayList<QuestionChoice> choices = new ArrayList();
    for (int i = 0; i < choiceText.size(); i++) {
      QuestionChoice newChoice =
          new QuestionChoice(choiceText.get(i), quizCareerPath, isAccepted.get(i));
      choices.add(newChoice);
    }
    return new QuizQuestion(question, choices);
  }

  private void handleInvalidInputError(String message, HttpServletResponse response)
      throws IOException {
    // this message will be turned into an alert box
    response.setContentType(JSON_CONTENT_TYPE);
    response.getWriter().println(this.gson.toJson(message));
  }
}
