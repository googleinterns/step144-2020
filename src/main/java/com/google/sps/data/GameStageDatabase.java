package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/* Game Stage database, to put and query for GameStages */
public class GameStageDatabase {
  private DatastoreService datastore;
  private static String QUERY_FOR_GAMESTAGE_ENTITY = "gamestage";
  private static Query query;
  private String NAME_PROPERTY = "name";
  private String ID_PROPERTY = "id";
  private String CONTENT_PROPERTY = "content";
  private String QUIZ_KEY_PROPERTY = "quizkey";
  private String IS_LAST_STAGE_PROPERTY = "isFinalStage";
  private String NEXT_STAGE_PROPERTY = "nextstage";

  public GameStageDatabase(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /* method to return a GameStage by level and career path */
  public GameStage getGameStage(String careerPath, Integer level) {
    String id = careerPath + level.toString();
    Entity gameStageEntity = getGameStageEntityFromID(id);
    GameStage gameStage = createGameStageFromEntity(gameStageEntity);
    return gameStage;
  }

  /* method to return a GameStage by just the id */
  public GameStage getGameStage(String id) {
    Entity gameStageEntity = getGameStageEntityFromID(id);
    GameStage gameStage = createGameStageFromEntity(gameStageEntity);
    return gameStage;
  }

  public void storeGameStage(GameStage gameStage) {
    Entity gameStageEntity = createGameStageEntity(gameStage);
    this.datastore.put(gameStageEntity);
  }

  private Entity getGameStageEntityFromID(String id) {
    Query query =
        new Query(QUERY_FOR_GAMESTAGE_ENTITY)
            .setFilter(new Query.FilterPredicate(ID_PROPERTY, Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    return entity;
  }

  private Entity createGameStageEntity(GameStage gameStage) {
    String gameStageKey = gameStage.getID();
    Entity entity = new Entity(QUERY_FOR_GAMESTAGE_ENTITY, gameStageKey);
    entity.setProperty(NAME_PROPERTY, gameStage.getName());
    entity.setProperty(ID_PROPERTY, gameStage.getID());
    entity.setProperty(CONTENT_PROPERTY, gameStage.getContent());
    entity.setProperty(QUIZ_KEY_PROPERTY, gameStage.getQuizKey());
    entity.setProperty(IS_LAST_STAGE_PROPERTY, gameStage.isFinalStage());
    entity.setProperty(NEXT_STAGE_PROPERTY, gameStage.getNextStageID());
    return entity;
  }

  private GameStage createGameStageFromEntity(Entity entity) {
    String name = entity.getProperty(NAME_PROPERTY).toString();
    String content = entity.getProperty(CONTENT_PROPERTY).toString();
    String id = entity.getProperty(ID_PROPERTY).toString();
    String quizKey = entity.getProperty(QUIZ_KEY_PROPERTY).toString();
    Boolean isFinalStage =
        Boolean.parseBoolean(entity.getProperty(IS_LAST_STAGE_PROPERTY).toString());
    String nextStageID = entity.getProperty(NEXT_STAGE_PROPERTY).toString();
    return new GameStage(name, content, id, quizKey, isFinalStage, nextStageID);
  }
}
