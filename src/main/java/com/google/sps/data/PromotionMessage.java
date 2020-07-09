package com.google.sps.data;

public class PromotionMessage {
  private Boolean isPromoted;
  private String promotionMessage;

  public PromotionMessage(Boolean isPromoted, String promotionMessage) {
    this.isPromoted = isPromoted;
    this.promotionMessage = promotionMessage;
  }
}
