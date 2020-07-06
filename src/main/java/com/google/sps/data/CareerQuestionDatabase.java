package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;

/* purpose: to manage the interface of the Career Question database */
public final class CareerQuestionDatabase {
  private DatastoreService datastore;
  private ArrayList<CareerQuestionAndChoices> questionAndChoices;
  private static final String ENTITY_QUERY_STRING = "careerquizquestionandchoices";
  private static final String QUESTION_QUERY_STRING = "question";
  private static final String CHOICE_QUERY_STRING = "choice";
  private static final String CHOICETEXT_QUERY_STRING = "choicetext";
  private static final String ASSOCIATED_CAREER_PATH_QUERY_STRING = "associatedcareerpath";
  private static final Query query = new Query(ENTITY_QUERY_STRING);

  public CareerQuestionDatabase(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /* method to return all questions and choices from database as an arraylist */
  public ArrayList<CareerQuestionAndChoices> getQuestionsAndChoices() {
    ArrayList<CareerQuestionAndChoices> careerQuestionAndChoicesList =
        new ArrayList<CareerQuestionAndChoices>();
    List<Entity> entities =
        this.datastore.prepare(this.query).asList(FetchOptions.Builder.withDefaults());
    for (Entity entity : entities) {
      CareerQuestionAndChoices careerQuestionAndChoices =
          this.entityToCareerQuestionAndChoices(entity);
      careerQuestionAndChoicesList.add(careerQuestionAndChoices);
    }
    return careerQuestionAndChoicesList;
  }

  public void putCareerQuestionAndChoicesIntoDatabase(
      CareerQuestionAndChoices careerQuestionAndChoices) {
    String question = careerQuestionAndChoices.getQuestion();
    Entity questionEntity = getQuestionDatastoreEntity(careerQuestionAndChoices);
    this.datastore.put(questionEntity);
    for (CareerQuestionChoice choice : careerQuestionAndChoices.getChoices()) {
      Entity choiceEntity = getChoiceDatastoreEntity(question, choice);
      this.datastore.put(choiceEntity);
    }
  }

  private CareerQuestionAndChoices entityToCareerQuestionAndChoices(Entity entity) {
    String question = entity.getProperty(QUESTION_QUERY_STRING).toString();
    Query associatedChoicesQuery = new Query(question + CHOICETEXT_QUERY_STRING);
    List<Entity> choiceEntities =
        this.datastore.prepare(associatedChoicesQuery).asList(FetchOptions.Builder.withDefaults());
    List<CareerQuestionChoice> choices = extractChoicesFromChoiceEntities(choiceEntities);
    return new CareerQuestionAndChoices(question, choices);
  }

  private List<CareerQuestionChoice> extractChoicesFromChoiceEntities(List<Entity> choiceEntities) {
    List<CareerQuestionChoice> choices = new ArrayList();
    for (Entity choiceEntity : choiceEntities) {
      String choiceText = choiceEntity.getProperty(CHOICETEXT_QUERY_STRING).toString();
      String associatedCareerPath =
          choiceEntity.getProperty(ASSOCIATED_CAREER_PATH_QUERY_STRING).toString();
      CareerQuestionChoice careerQuestionChoice =
          new CareerQuestionChoice(choiceText, associatedCareerPath);
      choices.add(careerQuestionChoice);
    }
    return choices;
  }

  private Entity getQuestionDatastoreEntity(CareerQuestionAndChoices careerQuestionAndChoices) {
    String questionKey = careerQuestionAndChoices.getQuestion();
    Entity entity = new Entity(ENTITY_QUERY_STRING, questionKey);
    entity.setProperty(QUESTION_QUERY_STRING, careerQuestionAndChoices.getQuestion());
    return entity;
  }

  private Entity getChoiceDatastoreEntity(
      String associatedQuestion, CareerQuestionChoice careerQuestionChoice) {
    String choiceEntityType = associatedQuestion + CHOICETEXT_QUERY_STRING;
    Entity entity = new Entity(choiceEntityType);
    entity.setProperty(CHOICETEXT_QUERY_STRING, careerQuestionChoice.getChoiceText());
    entity.setProperty(
        ASSOCIATED_CAREER_PATH_QUERY_STRING, careerQuestionChoice.getAssociatedCareerPath());
    return entity;
  }
}
