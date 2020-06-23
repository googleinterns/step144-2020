package com.google.sps.data;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* purpose: to manage the interface of the Career Question database */
public final class CareerQuestionDatabase {
 
  private DatastoreService datastore;
  private ArrayList<CareerQuestionAndChoices> questionAndChoices;
  private static final List<CareerQuestionChoice> HARD_CODED_CHOICES = Arrays.asList(
      new CareerQuestionChoice("choice1", "career1"),
      new CareerQuestionChoice("choice2", "career2")
    );
  
  public CareerQuestionDatabase(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /* method to return all questions and choices from database as an arraylist */
  public ArrayList<CareerQuestionAndChoices> getQuestionsAndChoices() {
  // TODO: replace hard coded example Q and Reponse with entities queried from datastore
    CareerQuestionAndChoices hard_coded_q_and_choice = 
        new CareerQuestionAndChoices("A question?", HARD_CODED_CHOICES);
    questionAndChoices = new ArrayList();
    questionAndChoices.add(hard_coded_q_and_choice);
  // REPLACE ABOVE

    return questionAndChoices;
  }
}
