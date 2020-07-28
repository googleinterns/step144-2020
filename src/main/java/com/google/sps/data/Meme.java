package com.google.sps.data;

// Memes serve as rewards in the game for players to collect.
public class Meme {
  private String title;
  private String source;
  private String id;

  public Meme(String title, String source, String id) {
    this.title = title;
    this.source = source;
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public String getSource() {
    return source;
  }

  public String getID() {
    return id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setID(String id) {
    this.id = id;
  }
}
