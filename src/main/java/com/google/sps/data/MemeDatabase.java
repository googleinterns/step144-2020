package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;

// Database for all memes to be accessed
public class MemeDatabase {
  private DatastoreService datastore;
  public List<Meme> memes = new ArrayList<>();
  private Query query;
  public static final String ENTITY_QUERY_STRING = "meme";
  public static final String TITLE_QUERY_STRING = "title";
  public static final String SOURCE_QUERY_STRING = "source";
  public static final String ID_QUERY_STRING = "id";

  public MemeDatabase(DatastoreService datastore) {
    this.datastore = datastore;
    query = new Query(ENTITY_QUERY_STRING);
  }

  public void addMemeToDatabase(Meme meme) {
    this.memes.add(meme);
    try {
      Entity memeEntity = memeToEntity(meme);
      datastore.put(memeEntity);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  public Entity memeToEntity(Meme meme) {
    Entity memeEntity = new Entity(ENTITY_QUERY_STRING, meme.getID());
    memeEntity.setProperty(TITLE_QUERY_STRING, meme.getTitle());
    memeEntity.setProperty(SOURCE_QUERY_STRING, meme.getSource());
    memeEntity.setProperty(ID_QUERY_STRING, meme.getID());
    return memeEntity;
  }

  public Meme entityToMeme(Entity entity) {
    String title = entity.getProperty(TITLE_QUERY_STRING).toString();
    String source = entity.getProperty(SOURCE_QUERY_STRING).toString();
    String id = entity.getKey().getName();
    Meme meme = new Meme(title, source, id);
    return meme;
  }

  public Entity getEntityFromID(String id) throws EntityNotFoundException {
    Key key = KeyFactory.createKey(ENTITY_QUERY_STRING, id);
    Entity entity = this.datastore.get(key);
    return entity;
  }

  public Meme getMemeFromID(String id) throws EntityNotFoundException {
    Entity entity = getEntityFromID(id);
    Meme meme = entityToMeme(entity);
    return meme;
  }
}
