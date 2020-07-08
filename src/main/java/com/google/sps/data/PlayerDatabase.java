
package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Player;
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

  // get entity
  public Entity getCurrentPlayerEntity() {
    if (UserServiceFactory.getUserService().getCurrentUser() != null) {
      User currentUser = UserServiceFactory.getUserService().getCurrentUser();
      String email = currentUser.getEmail();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query =
          new Query(ENTITY_QUERY_STRING)
              .setFilter(
                  new Query.FilterPredicate(
                      ID_QUERY_STRING, Query.FilterOperator.EQUAL, currentUser.getUserId()));
      PreparedQuery results = datastore.prepare(query);
      for (Entity entity : results.asIterable()) {
        if (email.equals(entity.getProperty(EMAIL_QUERY_STRING).toString())) {
          return entity;
        }
      }
    }
    return null;
  }

  // get player current stage
  private String getEntityCurrentPageID() {
    Entity currentPlayerEntity = getCurrentPlayerEntity();
    String currentPageID = currentPlayerEntity.getProperty(CURRENT_PAGE_ID_QUERY_STRING).toString();
    return currentPageID;
  }

  // get image id
  private String getEntityImageID() {
    Entity currentPlayerEntity = getCurrentPlayerEntity();
    String imageID = currentPlayerEntity.getProperty(IMAGE_ID_QUERY_STRING).toString();
    return imageID;
  }

  // get id
  private String getEntityID() {
    return USER.getUserId();
  }

  // get displayname
  public String getEntityDisplayName() {
    Entity currentPlayerEntity = getCurrentPlayerEntity();
    String displayName = currentPlayerEntity.getProperty(DISPLAY_NAME_QUERY_STRING).toString();
    return displayName;
  }
}