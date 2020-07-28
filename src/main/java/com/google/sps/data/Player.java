package com.google.sps.data;

import java.util.List;

public class Player {
  private String displayName;
  private String email;
  private String id;
  private String imageID;
  private String currentPageID;
  private List<String> allAccessoryIDs; // holds all accessories player has earned
  private String equippedHatID; // accessory id of currently equipped hat
  private String equippedGlassesID; // accessory id of currently equipped glasses
  private String equippedCompanionID; // accessory id of currently equipped companion
  /**
   * Experience Points are accumulated by players as they play the game. The amount of points a
   * player has determines when they can try for promotion, when a special event may occur, or when
   * the player recieves a reward. They are a scoring metric.
   */
  private int experiencePoints;

  public Player(String displayName, String email, String imageID) {
    this.displayName = displayName;
    this.email = email;
    this.imageID = imageID;
  }

  public Player(String displayName, String email, String id, String imageID, String currentPageID) {
    this(displayName, email, imageID);
    this.id = id;
    this.currentPageID = currentPageID;
  }

  public Player(
      String displayName,
      String email,
      String id,
      String imageID,
      String currentPageID,
      List<String> allAccessoryIDs,
      int experiencePoints) {
    this(displayName, email, id, imageID, currentPageID);
    this.allAccessoryIDs = allAccessoryIDs;
    this.experiencePoints = experiencePoints;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getEmail() {
    return email;
  }

  public String getID() {
    return id;
  }

  public String getImageID() {
    return imageID;
  }

  public String getCurrentPageID() {
    return currentPageID;
  }

  public int getExperiencePoints() {
    return experiencePoints;
  }

  public List<String> getAllAccessoryIDs() {
    return this.allAccessoryIDs;
  }

  public String getEquippedHatID() {
    return this.equippedHatID;
  }

  public String getEquippedGlassesID() {
    return this.equippedGlassesID;
  }

  public String getEquippedCompanionID() {
    return this.equippedCompanionID;
  }

  public void setEquippedCompanionID(String companionID) {
    this.equippedCompanionID = companionID;
  }

  public void setEquippedHatID(String hatID) {
    this.equippedHatID = hatID;
  }

  public void setEquippedGlassesID(String glassesID) {
    this.equippedGlassesID = glassesID;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setID(String id) {
    this.id = id;
  }

  public void setImageID(String imageID) {
    this.imageID = imageID;
  }

  public void setCurrentPageID(String currentPageID) {
    this.currentPageID = currentPageID;
  }

  public void setExperiencePoints(int experiencePoints) {
    this.experiencePoints = experiencePoints;
  }
}
