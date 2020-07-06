package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import java.util.List;

/* purpose: to manage the interface of the Career Question database */
public final class CareerQuestionDatabase extends QuestionDatabase<CareerQuestionChoice> {
  private DatastoreService datastore;
  private ArrayList<QuestionAndChoices<CareerQuestionChoice>> questionAndChoices;
  private static final String ENTITY_QUERY_STRING = "careerquizquestionandchoices";
  private static final String ASSOCIATED_CAREER_PATH_QUERY_STRING = "associatedcareerpath";

  public CareerQuestionDatabase(DatastoreService datastore) {
    super(datastore, ENTITY_QUERY_STRING);
  }

  @Override
  List<CareerQuestionChoice> extractChoicesFromChoiceEntities(List<Entity> choiceEntities) {
    List<CareerQuestionChoice> choices = new ArrayList();
    for (Entity choiceEntity : choiceEntities) {
      String choiceText = choiceEntity.getProperty(super.CHOICETEXT_QUERY_STRING).toString();
      String associatedCareerPath =
          choiceEntity.getProperty(ASSOCIATED_CAREER_PATH_QUERY_STRING).toString();
      CareerQuestionChoice careerQuestionChoice =
          new CareerQuestionChoice(choiceText, associatedCareerPath);
      choices.add(careerQuestionChoice);
    }
    return choices;
  }

  @Override
  Entity getChoiceDatastoreEntity(
      String associatedQuestion, CareerQuestionChoice careerQuestionChoice) {
    String choiceEntityType = associatedQuestion + super.CHOICETEXT_QUERY_STRING;
    Entity entity = new Entity(choiceEntityType);
    entity.setProperty(super.CHOICETEXT_QUERY_STRING, careerQuestionChoice.getChoiceText());
    entity.setProperty(
        ASSOCIATED_CAREER_PATH_QUERY_STRING, careerQuestionChoice.getAssociatedCareerPath());
    return entity;
  }
}
