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
  private static final String EXPERIENCE_QUERY_STRING = "experience";
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
  public Entity getCurrentPlayerEntity() throws LoggedOutException {
    if (!isLoggedIn) {
      throw new LoggedOutException();
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
    throw new LoggedOutException();
  }

  // get player current stage
  public String getEntityCurrentPageID() throws LoggedOutException {
    String currentPageID =
        getCurrentPlayerEntity().getProperty(CURRENT_PAGE_ID_QUERY_STRING).toString();
    return currentPageID;
  }

  // get image id
  public String getEntityImageID() throws LoggedOutException {
    String imageID = getCurrentPlayerEntity().getProperty(IMAGE_ID_QUERY_STRING).toString();
    return imageID;
  }

  // get id
  public String getEntityID() throws LoggedOutException {
    String id = getCurrentPlayerEntity().getProperty(ID_QUERY_STRING).toString();
    return id;
  }

  // get experience
  public int getEntityExperience() throws LoggedOutException {
    int experience =
        Integer.parseInt(getCurrentPlayerEntity().getProperty(EXPERIENCE_QUERY_STRING).toString());
    return experience;
  }

  // get displayname
  public String getEntityDisplayName() throws LoggedOutException {
    String displayName = getCurrentPlayerEntity().getProperty(DISPLAY_NAME_QUERY_STRING).toString();
    return displayName;
  }

  // set player current stage
  public void setEntityCurrentPageID(String currentPageID) throws LoggedOutException {
    setPlayerProperty(CURRENT_PAGE_ID_QUERY_STRING, currentPageID);
  }

  // set image id
  public void setEntityImageID(String imageID) throws LoggedOutException {
    setPlayerProperty(IMAGE_ID_QUERY_STRING, imageID);
  }

  // set id
  public void setEntityID(String id) throws LoggedOutException {
    setPlayerProperty(ID_QUERY_STRING, id);
  }

  // set experience
  public void setEntityExperience(int experience) throws LoggedOutException {
    setPlayerProperty(EXPERIENCE_QUERY_STRING, Integer.toString(experience));
  }

  // set displayname
  public void setEntityDisplayName(String displayName) throws LoggedOutException {
    setPlayerProperty(DISPLAY_NAME_QUERY_STRING, displayName);
  }

  private void setPlayerProperty(String propertyName, String newValue) throws LoggedOutException {
    Entity entity = getCurrentPlayerEntity();
    entity.setProperty(propertyName, newValue);
    datastore.put(entity);
  }
}
