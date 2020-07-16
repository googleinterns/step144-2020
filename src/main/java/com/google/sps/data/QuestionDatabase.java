package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;

/* abstract quiz question database, to put and query for QuestionAndChoices */
public class QuestionDatabase {
  private DatastoreService datastore;
  private ArrayList<QuizQuestion> quizQuestions;
  private String entityQueryString;
  private Query query;
  public static final String QUESTION_QUERY_STRING = "question";
  public static final String CHOICETEXT_QUERY_STRING = "choiceText";
  private static final String ASSOCIATED_CAREER_PATH_QUERY_STRING = "associatedCareerPath";
  private static final String IS_ACCEPTED_QUERY_STRING = "isAccepted";

  public QuestionDatabase(DatastoreService datastore, String entityQueryString) {
    this.datastore = datastore;
    this.entityQueryString = entityQueryString;
    this.query = new Query(entityQueryString);
  }

  /* method to return all questions and choices from database as an arraylist */
  public ArrayList<QuizQuestion> getQuizQuestions() {
    ArrayList<QuizQuestion> quizQuestionsList = new ArrayList();
    List<Entity> entities =
        this.datastore.prepare(this.query).asList(FetchOptions.Builder.withDefaults());
    for (Entity entity : entities) {
      QuizQuestion quizQuestions = this.entityToQuizQuestion(entity);
      quizQuestionsList.add(quizQuestions);
    }
    return quizQuestionsList;
  }

  public void putQuizQuestionsIntoDatabase(QuizQuestion quizQuestions) {
    String question = quizQuestions.getQuestion();
    Entity questionEntity = getQuestionDatastoreEntity(quizQuestions);
    this.datastore.put(questionEntity);
    for (QuestionChoice choice : quizQuestions.getChoices()) {
      Entity choiceEntity = getChoiceDatastoreEntity(question, choice);
      this.datastore.put(choiceEntity);
    }
  }

  private QuizQuestion entityToQuizQuestion(Entity entity) {
    String question = entity.getProperty(QUESTION_QUERY_STRING).toString();
    Query associatedChoicesQuery = new Query(question + CHOICETEXT_QUERY_STRING);
    List<Entity> choiceEntities =
        this.datastore.prepare(associatedChoicesQuery).asList(FetchOptions.Builder.withDefaults());
    List<QuestionChoice> choices = extractChoicesFromChoiceEntities(choiceEntities);
    return new QuizQuestion(question, choices);
  }

  private Entity getQuestionDatastoreEntity(QuizQuestion quizQuestions) {
    String questionKey = quizQuestions.getQuestion();
    Entity entity = new Entity(entityQueryString, questionKey);
    entity.setProperty(QUESTION_QUERY_STRING, quizQuestions.getQuestion());
    return entity;
  }

  private List<QuestionChoice> extractChoicesFromChoiceEntities(List<Entity> choiceEntities) {
    List<QuestionChoice> choices = new ArrayList();
    for (Entity choiceEntity : choiceEntities) {
      String choiceText = choiceEntity.getProperty(CHOICETEXT_QUERY_STRING).toString();
      String associatedCareerPath =
          choiceEntity.getProperty(ASSOCIATED_CAREER_PATH_QUERY_STRING).toString();
      Boolean isAcceptedChoice =
          Boolean.parseBoolean(choiceEntity.getProperty(IS_ACCEPTED_QUERY_STRING).toString());
      QuestionChoice questionChoice =
          new QuestionChoice(choiceText, associatedCareerPath, isAcceptedChoice);
      choices.add(questionChoice);
    }
    return choices;
  }

  private Entity getChoiceDatastoreEntity(String associatedQuestion, QuestionChoice choice) {
    String choiceEntityType = associatedQuestion + CHOICETEXT_QUERY_STRING;
    Entity entity = new Entity(choiceEntityType);
    entity.setProperty(CHOICETEXT_QUERY_STRING, choice.getChoiceText());
    entity.setProperty(ASSOCIATED_CAREER_PATH_QUERY_STRING, choice.getAssociatedCareerPath());
    entity.setProperty(IS_ACCEPTED_QUERY_STRING, Boolean.toString(choice.getIsAcceptableChoice()));
    return entity;
  }
}
