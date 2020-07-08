//this will hold al the needed dialogue
package com.google.sps.data;

import java.util.ArrayList;

public final class GameDialogue{
  private final String currentPageId;
  private final int dialogueIndex;
  public static ArrayList<String> softwareEngineeringDialogue = new ArrayList<>();
  public static ArrayList<String> webDevelopmentDialogue = new ArrayList<>();
  public static ArrayList<String> programMannagementDialogue = new ArrayList<>();
  public static ArrayList<String> dataScienceDialogue = new ArrayList<>();
  private int lengthOfDialogue = 1;

  public GameDialogue(String currentPageId, int dialogueIndex) {
    this.currentPageId = currentPageId;
    this.dialogueIndex = dialogueIndex;
  }

  public String getDialogue(int dialogueIndex) {
    if(this.currentPageId.equals("software-engineering-0")) {
      softwareEngineeringDialogue.add("index0");
      softwareEngineeringDialogue.add("index1");
      softwareEngineeringDialogue.add("You Will now take a quiz my student");
      softwareEngineeringDialogue.add("You do not have access to the requested resource Error Code 4: Rebooting.......");
      setlengthofDialogue(softwareEngineeringDialogue.size());
      return softwareEngineeringDialogue.get(dialogueIndex);
    } else if(this.currentPageId.equals("web-development-0")) {
      webDevelopmentDialogue.add("Error 404 not found");
    } else if(this.currentPageId.equals("program-mannagement-0")) {
      programMannagementDialogue.add("Error 404 not found");
    } else if(this.currentPageId.equals("data-scientist-0")) {
      dataScienceDialogue.add("Error 404 not found");
    }
    return "You do not have access to the requested resource Error Code 4: UberProxy not found";
  }

  public int getlengthofDialogue() {
    return this.lengthOfDialogue;
  }

  private void setlengthofDialogue(int lengthOfDialogue) {
    this.lengthOfDialogue = lengthOfDialogue;
  }

  public String getDialogueFrompageID(String currentPageId){
      return "this is the current pageID" + currentPageId;
  }
}
