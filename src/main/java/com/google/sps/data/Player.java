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
  /**
   * In order for a player to try for promotion, a player's experience points must reach an ever
   * growing threshold. This threshold is specific to each player, and relative to their experience
   * points number. When a player tries for promotion, the promotionThreshold increases, so a player
   * has to do more work to try for the next promotion
   */
  private int promotionThreshold;

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
      int experiencePoints,
      int promotionThreshold) {
    this(displayName, email, id, imageID, currentPageID);
    this.allAccessoryIDs = allAccessoryIDs;
    this.experiencePoints = experiencePoints;
    this.promotionThreshold = promotionThreshold;
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

  public int getPromotionThreshold() {
    return this.promotionThreshold;
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

  public void setPromotionThreshold(int promotionThreshold) {
    this.promotionThreshold = promotionThreshold;
  }
}
