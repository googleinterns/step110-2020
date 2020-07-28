package com.google.ehub.data;

/**
 * Class that defines a CommentData object
 */
public class CommentData {
  private final Long itemId;
  private final String comment;
  private final Long timestampMillis;
  private final String username;
  private final Long commentId;
  private final boolean belongsToUser;

  public CommentData(long itemId, String comment, long timestampMillis, String username,
      long commentId, boolean belongsToUser) {
    this.itemId = itemId;
    this.comment = comment;
    this.timestampMillis = timestampMillis;
    this.username = username;
    this.commentId = commentId;
    this.belongsToUser = belongsToUser;
  }

   public boolean getBelongsToUser() {
    return this.belongsToUser;
  }
}
