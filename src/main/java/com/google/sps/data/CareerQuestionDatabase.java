package com.google.sps.data;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import java.util.ArrayList;

/* purpose: to manage the interface of the Career Question database */
public final class CareerQuestionDatabase {
 
  private DatastoreService datastore;
  private ArrayList<CareerQuestionAndChoices> questionAndChoices;
  
  public CareerQuestionDatabase(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /* method to return all questions and choices from database as an arraylist */
  public ArrayList<CareerQuestionAndChoices> getQuestionsAndChoices() {
  // TODO: replace hard coded example Q and Reponse with entities queried from datastore
    CareerQuestionChoice HARD_CODED_CHOICE1 = new CareerQuestionChoice("choice1", "career1");
    CareerQuestionChoice HARD_CODED_CHOICE2 = new CareerQuestionChoice("choice2", "career2");
    ArrayList<CareerQuestionChoice> HARD_CODED_CHOICES = new ArrayList();
    HARD_CODED_CHOICES.add(HARD_CODED_CHOICE1);
    HARD_CODED_CHOICES.add(HARD_CODED_CHOICE2);
    CareerQuestionAndChoices HARD_CODED_Q_AND_CHOICE = 
        new CareerQuestionAndChoices("A question?", HARD_CODED_CHOICES);
    questionAndChoices = new ArrayList();
    questionAndChoices.add(HARD_CODED_Q_AND_CHOICE);
  // REPLACE ABOVE

    return questionAndChoices;
  }
}
