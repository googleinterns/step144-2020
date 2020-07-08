// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.sps.data;

import com.google.appengine.api.datastore.Key;

public class GameStage {
  /**
   * Game Stages are levels on the gameboard. Each level can be accessed by a player, but only one
   * at a time.
   */
  private String name;
  /** Represents the title of the game stage */
  private String id;
  /** Represents the datastore ID */
  private String content;
  /** Represents what text content will be displayed once game stage is loaded */
  private Key quizKey;
  /** Represents key of the quiz that is specific to this game stage */

    private String name;
    /** Represents the title of the game stage 
    */
    private String id;
    /** Represents the datastore ID
    */
    private String content;
    /** Represents what text content will be displayed once game stage is loaded 
    */
    private Key quizKey;
    /** Represents key of the quiz that is specific to this game stage 
    */
    private int dialogueKey = 0;
    
  /**
   * Creates a game stage with the specified name and content
   *
   * @param name The name of the game stage
   * @param content The text content tied to the game stage
   */
  public GameStage(String name, String content) {
    this.name = name;
    this.content = content;
  }

  /**
   * Gets the game stage's name
   *
   * @return A string representing the title of the game stage
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the game stage's ID
   *
   * @return A string representing the datastore ID of the game stage
   */
  public String getID() {
    return id;
  }

  /**
   * Gets the game stage's content
   *
   * @return A string representing the text content of the game stage
   */
  public String getContent() {
    return content;
  }

    /** Sets the game stage's content
    @param content A string representing the text content of the game stage 
    */
    public void setContent(String content) { 
        this.content = content;
    }
 
    /** Sets the game stage's quiz key
    @param quizKey A string representing the quiz key of the game stage 
    */
    public void setQuizKey(Key quizKey) {
        this.quizKey = quizKey;
    }

    public void setdialogueKey(int dialoguekey){
        this.dialogueKey = dialogueKey;
    }

    public int getdialogueKey(){
        return dialogueKey;
    }
}
