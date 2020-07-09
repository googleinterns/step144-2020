package com.google.sps.data;

public class Player {
  private String displayName;
  private String email;
  private String id;
  private String imageID;
  private String currentPageID;

  public Player(String displayName, String email) {
    this.displayName = displayName;
    this.email = email;
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
}
