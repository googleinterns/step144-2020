package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.ArrayList;
import java.util.List;

// Manages the interface of the Player database for testing
public class PlayerDatabase {
  private static DatastoreService datastore;
  public static List<Player> players = new ArrayList<>();
  private static final String ENTITY_QUERY_STRING = "player";
  private static final String DISPLAY_NAME_QUERY_STRING = "displayName";
  private static final String EMAIL_QUERY_STRING = "email";
  private static final String ID_QUERY_STRING = "id";
  private static final String IMAGE_ID_QUERY_STRING = "imageID";
  private static final String CURRENT_PAGE_ID_QUERY_STRING = "currentPageID";
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final Query query = new Query(ENTITY_QUERY_STRING);
  private User user;
  private String userEmail = UserServiceFactory.getUserService().getCurrentUser().getEmail();
  private String userID = UserServiceFactory.getUserService().getCurrentUser().getUserId();
  private boolean isLoggedIn;

  public static List<Player> getPlayers() {
    return players;
  }

  public PlayerDatabase(DatastoreService datastore) {
    this(datastore, UserServiceFactory.getUserService());
  }

  public PlayerDatabase(DatastoreService datastore, UserService userService) {
    this.datastore = datastore;
    this.user = userService.getCurrentUser();
    this.isLoggedIn = userService.isUserLoggedIn();
  }

  public static void addPlayerToDatabase(Player player) {
    players.add(player);
    Entity entity = createEntityFromPlayer(player);
    datastore.put(entity);
  }

  public static Entity createEntityFromPlayer(Player player) {
    String displayName = player.getDisplayName();
    String id = player.getID();
    String email = player.getEmail();
    String imageID = player.getImageID();
    String currentPageID = player.getCurrentPageID();
    Entity entity = new Entity(ENTITY_QUERY_STRING);
    entity.setProperty(DISPLAY_NAME_QUERY_STRING, displayName);
    entity.setProperty(EMAIL_QUERY_STRING, email);
    entity.setProperty(ID_QUERY_STRING, id);
    entity.setProperty(IMAGE_ID_QUERY_STRING, imageID);
    entity.setProperty(CURRENT_PAGE_ID_QUERY_STRING, currentPageID);
    return entity;
  }

  // get entity
  public Entity getCurrentPlayerEntity() throws Exception {
    if (!isLoggedIn) {
      throw new Exception(LOGGED_OUT_EXCEPTION);
    }
    String email = userEmail;
    Query query =
        new Query(ENTITY_QUERY_STRING)
            .setFilter(
                new Query.FilterPredicate(ID_QUERY_STRING, Query.FilterOperator.EQUAL, userID));
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      if (email.equals(entity.getProperty(EMAIL_QUERY_STRING).toString())) {
        return entity;
      }
    }
    throw new Exception(LOGGED_OUT_EXCEPTION);
  }

  // get player current stage
  public String getEntityCurrentPageID() throws Exception {
    String currentPageID =
        getCurrentPlayerEntity().getProperty(CURRENT_PAGE_ID_QUERY_STRING).toString();
    return currentPageID;
  }

  // get image id
  public String getEntityImageID() throws Exception {
    String imageID = getCurrentPlayerEntity().getProperty(IMAGE_ID_QUERY_STRING).toString();
    return imageID;
  }

  // get id
  public String getEntityID() throws Exception {
    String id = getCurrentPlayerEntity().getProperty(ID_QUERY_STRING).toString();
    return id;
  }

  // get displayname
  public String getEntityDisplayName() throws Exception {
    String displayName = getCurrentPlayerEntity().getProperty(DISPLAY_NAME_QUERY_STRING).toString();
    return displayName;
  }

  // set player current stage
  public void setEntityCurrentPageID(String currentPageID) throws Exception {
    setPlayerProperty(CURRENT_PAGE_ID_QUERY_STRING, currentPageID);
  }

  // set image id
  public void setEntityImageID(String imageID) throws Exception {
    setPlayerProperty(IMAGE_ID_QUERY_STRING, imageID);
  }

  // set id
  public void setEntityID(String id) throws Exception {
    setPlayerProperty(ID_QUERY_STRING, id);
  }

  // set displayname
  public void setEntityDisplayName(String displayName) throws Exception {
    setPlayerProperty(DISPLAY_NAME_QUERY_STRING, displayName);
  }

  private void setPlayerProperty(String propertyName, String newValue) throws Exception {
    Entity entity = getCurrentPlayerEntity();
    entity.setProperty(propertyName, newValue);
    datastore.put(entity);
  }
}