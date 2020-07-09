package com.google.sps.data;

import java.util.HashMap;
import java.util.List;

/** Returns the recommended career path based off the career quiz results. */
public class ProcessCareerQuizResults {
  private ProcessCareerQuizResults() {}

  public static String getRecommendedCareerPath(List<QuestionChoice> userChoices) {
    HashMap<String, Integer> choiceFrequencyHashMap = new HashMap<String, Integer>();
    String careerPath;
    String maxFrequencyCareerPath = new String();
    Integer count;
    // creates a frequency choiceFrequencyMap mapping associated career paths of choices to their
    // frequencies
    for (QuestionChoice choice : userChoices) {
      careerPath = choice.getAssociatedCareerPath();
      count = choiceFrequencyHashMap.getOrDefault(careerPath, 0);
      choiceFrequencyHashMap.put(careerPath, count + 1);
    }
    return getMostFrequentChoice(choiceFrequencyHashMap);
  }

  private static String getMostFrequentChoice(HashMap<String, Integer> choiceFrequencyMap) {
    int maxFrequencyCount = 0;
    int currentFrequency;
    String mostFrequentKey = "";
    for (String key : choiceFrequencyMap.keySet()) {
      currentFrequency = choiceFrequencyMap.get(key);
      if (currentFrequency > maxFrequencyCount) {
        mostFrequentKey = key;
        maxFrequencyCount = currentFrequency;
      }
    }
    return mostFrequentKey;
  }
}
