package com.google.ehub.data;

public class CommentData {
  private final Long itemId;
  private final String message;
  private final Long timestamp;

  public CommentData(long itemId,String message, long timestamp){
    this.itemId = itemId;
    this.message = message;
    this.timestamp = timestamp;
  }
  
}
