package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;

/* abstract quiz question database, to put and query for QuestionAndChoices */
public abstract class QuestionDatabase<T extends QuestionChoice> {
  private DatastoreService datastore;
  private ArrayList<QuestionAndChoices<T>> questionAndChoices;
  private static String entityQueryString;
  private static Query query;
  public static final String QUESTION_QUERY_STRING = "question";
  public static final String CHOICETEXT_QUERY_STRING = "choicetext";

  public QuestionDatabase(DatastoreService datastore, String entityQueryString) {
    this.datastore = datastore;
    this.entityQueryString = entityQueryString;
    this.query = new Query(entityQueryString);
  }

  /* method to return all questions and choices from database as an arraylist */
  public ArrayList<QuestionAndChoices<T>> getQuestionsAndChoices() {
    ArrayList<QuestionAndChoices<T>> questionAndChoicesList = new ArrayList();
    List<Entity> entities =
        this.datastore.prepare(this.query).asList(FetchOptions.Builder.withDefaults());
    for (Entity entity : entities) {
      QuestionAndChoices<T> questionAndChoices = this.entityToQuestionAndChoices(entity);
      questionAndChoicesList.add(questionAndChoices);
    }
    return questionAndChoicesList;
  }

  public void putCareerQuestionAndChoicesIntoDatabase(QuestionAndChoices<T> questionAndChoices) {
    String question = questionAndChoices.getQuestion();
    Entity questionEntity = getQuestionDatastoreEntity(questionAndChoices);
    this.datastore.put(questionEntity);
    for (T choice : questionAndChoices.getChoices()) {
      Entity choiceEntity = getChoiceDatastoreEntity(question, choice);
      this.datastore.put(choiceEntity);
    }
  }

  private QuestionAndChoices entityToQuestionAndChoices(Entity entity) {
    String question = entity.getProperty(QUESTION_QUERY_STRING).toString();
    Query associatedChoicesQuery = new Query(question + CHOICETEXT_QUERY_STRING);
    List<Entity> choiceEntities =
        this.datastore.prepare(associatedChoicesQuery).asList(FetchOptions.Builder.withDefaults());
    List<T> choices = extractChoicesFromChoiceEntities(choiceEntities);
    return new QuestionAndChoices<T>(question, choices);
  }

  private Entity getQuestionDatastoreEntity(QuestionAndChoices<T> questionAndChoices) {
    String questionKey = questionAndChoices.getQuestion();
    Entity entity = new Entity(entityQueryString, questionKey);
    entity.setProperty(QUESTION_QUERY_STRING, questionAndChoices.getQuestion());
    return entity;
  }

  abstract List<T> extractChoicesFromChoiceEntities(List<Entity> choiceEntities);

  abstract Entity getChoiceDatastoreEntity(String associatedQuestion, T choice);
}
