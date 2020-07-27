package com.google.sps.data;

public class Accessory {

  public enum Type {
    HAT,
    GLASSES,
    COMPANION
  };

  private String id; // id that will be used as key to query by
  private String imageFilePath; // filepath to image for accessory
  private Type type; // player will only be able to equip one of each type
  private int height;
  private int width;
  private int xPos; // position relative to player sprite container
  private int yPos;

  public Accessory(
      String id, String imageFilePath, Type type, int height, int width, int xPos, int yPos) {
    this.id = id;
    this.imageFilePath = imageFilePath;
    this.type = type;
    this.height = height;
    this.width = width;
    this.xPos = xPos;
    this.yPos = yPos;
  }

  public String getId() {
    return this.id;
  }

  public String getImageFilePath() {
    return this.imageFilePath;
  }

  public Type getType() {
    return this.type;
  }

  public int getHeight() {
    return this.height;
  }

  public int getWidth() {
    return this.width;
  }

  public int getXPos() {
    return this.xPos;
  }

  public int getYPos() {
    return this.yPos;
  }
}
