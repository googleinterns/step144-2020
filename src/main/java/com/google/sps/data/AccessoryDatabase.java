package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Accessory.Type;
import java.util.IllegalFormatException;

/* Accessory database, to put and query for Accessories */
public class AccessoryDatabase {
  private DatastoreService datastore;
  private static String QUERY_FOR_ACCESSORY_ENTITY = "accessory";
  private static Query query;
  private String ID_PROPERTY = "id";
  private String FILEPATH_PROPERTY = "filepath";
  private String TYPE_PROPERTY = "type";
  private String HEIGHT_PROPERTY = "height";
  private String WIDTH_PROPERTY = "width";
  private String XPOS_PROPERTY = "xPos";
  private String YPOS_PROPERTY = "yPos";

  public AccessoryDatabase(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /* method to return a Accessory by just the id */
  public Accessory getAccessory(String id) throws EntityNotFoundException {
    Entity accessoryEntity = getAccessoryEntityFromID(id);
    Accessory accessory = createAccessoryFromEntity(accessoryEntity);
    return accessory;
  }

  public void storeAccessory(Accessory accessory) {
    try {
      Entity accessoryEntity = createAccessoryEntity(accessory);
      this.datastore.put(accessoryEntity);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  private Entity getAccessoryEntityFromID(String id) throws EntityNotFoundException {
    Key key = KeyFactory.createKey(QUERY_FOR_ACCESSORY_ENTITY, id);
    Entity entity = this.datastore.get(key);
    return entity;
  }

  private Entity createAccessoryEntity(Accessory accessory) {
    String accessoryKey = accessory.getId();
    Entity entity = new Entity(QUERY_FOR_ACCESSORY_ENTITY, accessoryKey);
    entity.setProperty(FILEPATH_PROPERTY, accessory.getImageFilePath());
    entity.setProperty(TYPE_PROPERTY, accessory.getType().name());
    entity.setProperty(HEIGHT_PROPERTY, accessory.getHeight());
    entity.setProperty(WIDTH_PROPERTY, accessory.getWidth());
    entity.setProperty(XPOS_PROPERTY, accessory.getXPos());
    entity.setProperty(YPOS_PROPERTY, accessory.getYPos());
    return entity;
  }

  private Accessory createAccessoryFromEntity(Entity entity) throws IllegalArgumentException {
    String id = entity.getKey().getName();
    String imageFilePath = entity.getProperty(FILEPATH_PROPERTY).toString();
    Type type = getTypeFromProperty(entity, TYPE_PROPERTY);
    int height = getIntegerFromProperty(entity, HEIGHT_PROPERTY);
    int width = getIntegerFromProperty(entity, WIDTH_PROPERTY);
    int xPos = getIntegerFromProperty(entity, XPOS_PROPERTY);
    int yPos = getIntegerFromProperty(entity, YPOS_PROPERTY);
    return new Accessory(id, imageFilePath, type, height, width, xPos, yPos);
  }

  private Type getTypeFromProperty(Entity entity, String propertyQuery)
      throws IllegalFormatException {
    String typeValue = entity.getProperty(propertyQuery).toString();
    try {
      Type type = Type.valueOf(typeValue);
      return type;
    } catch (IllegalArgumentException e) {
      String message = typeValue + " cannot be parsed into type. (hat, glasses, companion)";
      throw new IllegalArgumentException(message, e);
    }
  }

  private int getIntegerFromProperty(Entity entity, String propertyQuery)
      throws NumberFormatException {
    String propertyValue = entity.getProperty(propertyQuery).toString();
    try {
      int value = Integer.parseInt(propertyValue);
      return value;
    } catch (IllegalArgumentException e) {
      String message =
          "Property "
              + propertyQuery
              + " has a string "
              + propertyValue
              + "that cannot be parsed into an int.";
      throw new IllegalArgumentException(message, e);
    }
  }
}
