package com.google.sps.data;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  public static ArrayList<Player> getPlayers() {
      return players;
  }
  
  public PlayerDatabase(DatastoreService datastore) {
      this.datastore = datastore;
  }
  public void addPlayerToDatabase(Player player) {
      players.add(player);
      String displayName = player.getDisplayName();
      String email = player.getEmail();
      String id = player.getID();
      String imageID = player.getImageID();
      String currentPageID = player.getCurrentPageID();
      Entity entity = new Entity(ENTITY_QUERY_STRING);
      entity.setProperty(DISPLAY_NAME_QUERY_STRING, displayName);
      entity.setProperty(EMAIL_QUERY_STRING, email);
      entity.setProperty(ID_QUERY_STRING, id);
      entity.setProperty(IMAGE_ID_QUERY_STRING, imageID);
      entity.setProperty(CURRENT_PAGE_ID_QUERY_STRING, currentPageID);
      this.datastore.put(entity);
  } 

  private static Player entityToPlayer(Entity entity) {
      String displayName = entity.getProperty(DISPLAY_NAME_QUERY_STRING).toString();
      String email = entity.getProperty(EMAIL_QUERY_STRING).toString();
      return new Player(displayName, email);
  }

}
