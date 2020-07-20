package com.google.ehub.data;

/**
 * Class that defines a CommentData object
 */
public class CommentData {
  private final Long itemId;
  private final String comment;
  private final Long timestampMillis;
  private final String email;

  public CommentData(long itemId, String comment, long timestampMillis, String email) {
    this.itemId = itemId;
    this.comment = comment;
    this.timestampMillis = timestampMillis;
    this.email = email;
  }
}
