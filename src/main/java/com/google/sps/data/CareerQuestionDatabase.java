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
  private ArrayList<QuestionAndChoices<CareerQuestionChoice>> questionAndChoices;
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
  public ArrayList<QuestionAndChoices<CareerQuestionChoice>> getQuestionsAndChoices() {
    ArrayList<QuestionAndChoices<CareerQuestionChoice>> questionAndChoicesList = new ArrayList();
    List<Entity> entities =
        this.datastore.prepare(this.query).asList(FetchOptions.Builder.withDefaults());
    for (Entity entity : entities) {
      QuestionAndChoices<CareerQuestionChoice> questionAndChoices =
          this.entityToQuestionAndChoices(entity);
      questionAndChoicesList.add(questionAndChoices);
    }
    return questionAndChoicesList;
  }

  public void putCareerQuestionAndChoicesIntoDatabase(
      QuestionAndChoices<CareerQuestionChoice> questionAndChoices) {
    String question = questionAndChoices.getQuestion();
    Entity questionEntity = getQuestionDatastoreEntity(questionAndChoices);
    this.datastore.put(questionEntity);
    for (CareerQuestionChoice choice : questionAndChoices.getChoices()) {
      Entity choiceEntity = getChoiceDatastoreEntity(question, choice);
      this.datastore.put(choiceEntity);
    }
  }

  private QuestionAndChoices entityToQuestionAndChoices(Entity entity) {
    String question = entity.getProperty(QUESTION_QUERY_STRING).toString();
    Query associatedChoicesQuery = new Query(question + CHOICETEXT_QUERY_STRING);
    List<Entity> choiceEntities =
        this.datastore.prepare(associatedChoicesQuery).asList(FetchOptions.Builder.withDefaults());
    List<CareerQuestionChoice> choices = extractChoicesFromChoiceEntities(choiceEntities);
    return new QuestionAndChoices<CareerQuestionChoice>(question, choices);
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

  private Entity getQuestionDatastoreEntity(
      QuestionAndChoices<CareerQuestionChoice> questionAndChoices) {
    String questionKey = questionAndChoices.getQuestion();
    Entity entity = new Entity(ENTITY_QUERY_STRING, questionKey);
    entity.setProperty(QUESTION_QUERY_STRING, questionAndChoices.getQuestion());
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
