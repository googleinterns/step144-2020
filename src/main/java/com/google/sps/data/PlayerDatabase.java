package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;

// Manages the interface of the Player database for testing
public final class PlayerDatabase {
  private static DatastoreService datastore;
  public static ArrayList<Player> players = new ArrayList<>();
  private static final String ENTITY_QUERY_STRING = "player";
  private static final String DISPLAY_NAME_QUERY_STRING = "displayName";
  private static final String EMAIL_QUERY_STRING = "email";
  private static final String ID_QUERY_STRING = "id";
  private static final String IMAGE_ID_QUERY_STRING = "imageID";
  private static final String CURRENT_PAGE_ID_QUERY_STRING = "currentPageID";
  private static final Query query = new Query(ENTITY_QUERY_STRING);
  private static final UserService USER_SERVICE = UserServiceFactory.getUserService();
  private static final User USER = USER_SERVICE.getCurrentUser();

  public static ArrayList<Player> getPlayers() {
    return players;
  }

  public PlayerDatabase(DatastoreService datastore) {
    this.datastore = datastore;
  }

  public static void addPlayerToDatabase(Player player, String id) {
    players.add(player);
    String displayName = player.getDisplayName();
    String email = player.getEmail();
    String imageID = player.getImageID();
    String currentPageID = player.getCurrentPageID();
    Entity entity = new Entity(ENTITY_QUERY_STRING);
    entity.setProperty(DISPLAY_NAME_QUERY_STRING, displayName);
    entity.setProperty(EMAIL_QUERY_STRING, email);
    entity.setProperty(ID_QUERY_STRING, id);
    entity.setProperty(IMAGE_ID_QUERY_STRING, imageID);
    entity.setProperty(CURRENT_PAGE_ID_QUERY_STRING, currentPageID);
    datastore.put(entity);
  }

  private static Player entityToPlayer(Entity entity) {
    String displayName = entity.getProperty(DISPLAY_NAME_QUERY_STRING).toString();
    String email = entity.getProperty(EMAIL_QUERY_STRING).toString();
    return new Player(displayName, email);
  }

  // get entity
  private Entity getCurrentPlayerEntity() {
    String email = USER.getEmail();
    if (UserServiceFactory.getUserService().getCurrentUser() != null) {
      User currentUser = UserServiceFactory.getUserService().getCurrentUser();
      String email = currentUser.getEmail();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query =
          new Query(PLAYER_QUERY_PARAMETER)
              .setFilter(
                  new Query.FilterPredicate(
                      ID_PARAMETER, Query.FilterOperator.EQUAL, currentUser.getUserId()));
      Key playerKey;
      PreparedQuery results = datastore.prepare(query);
      for (Entity entity : results.asIterable()) {
        if (email.equals(entity.getProperty(EMAIL_PARAMETER).toString())) {
          return entity;
        }
      }
    }
    return null;
  }

  // get player current stage
  private String getCurrentPageID() {
    String email = USER.getEmail();
    // Takes in the email of the current user, compares it to emails in database to find the corresponding image ID
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(ENTITY_QUERY_STRING).setFilter(new Query.FilterPredicate(ID_QUERY_STRING, Query.FilterOperator.EQUAL, USER.getUserId()));
    PreparedQuery results = datastore.prepare(query);
    String currentPageID = "";
    for (Entity entity : results.asIterable()){
      if (email.equals(entity.getProperty(EMAIL_QUERY_STRING).toString())){
        currentPageID = entity.getProperty(CURRENT_PAGE_ID_QUERY_STRING).toString();
      }
    }
    return currentPageID;
  }

  // get image id
  private String getImageID() {
    String email = USER.getEmail();
    // Takes in the email of the current user, compares it to emails in database to find the corresponding image ID
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(ENTITY_QUERY_STRING).setFilter(new Query.FilterPredicate(ID_QUERY_STRING, Query.FilterOperator.EQUAL, USER.getUserId()));
    PreparedQuery results = datastore.prepare(query);
    String imageID = "";
    for (Entity entity : results.asIterable()){
      if (email.equals(entity.getProperty(EMAIL_QUERY_STRING).toString())){
        imageID = entity.getProperty(IMAGE_ID_QUERY_STRING).toString();
      }
    }
    return imageID;
  }

  // get id
  private String getID() {
    return USER.getUserID();
  }

  // get displayname
  private String getDisplayName() {
    // Takes in the email of the current user, compares it to emails in database to find the corresponding nickname
    String email = USER.getEmail();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(ENTITY_QUERY_STRING).setFilter(new Query.FilterPredicate(ID_QUERY_STRING, Query.FilterOperator.EQUAL, USER.getUserId()));
    PreparedQuery results = datastore.prepare(query);
    String nickname = "";
    for (Entity entity : results.asIterable()){
      if (email.equals(entity.getProperty(EMAIL_QUERY_STRING).toString())){
        nickname = entity.getProperty(DISPLAY_NAME_QUERY_STRING).toString();
      }
    }
    return nickname;
  }

}
