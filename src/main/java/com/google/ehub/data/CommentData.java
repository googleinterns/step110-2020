package com.google.ehub.data;

public class CommentData {
  private final Long itemId;
  private final String message;
  private final Long timestampMillis;

  public CommentData(long itemId, String message, long timestampMillis) {
    this.itemId = itemId;
    this.message = message;
    this.timestampMillis = timestampMillis;
  }
}
