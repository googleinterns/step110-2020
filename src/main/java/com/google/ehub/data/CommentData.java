package com.google.ehub.data;

public class CommentData {
<<<<<<< HEAD
  private final Long itemId;
  private final String message;
  private final Long timestamp;

  public CommentData(long itemId,String message, long timestamp){
    this.itemId = itemId;
=======
  private final String message;
  private final Long timestamp;

  public CommentData(String message, long timestamp){
>>>>>>> 3ccab2bc0d5465bb8e2ae6e00009a398744dc0b3
    this.message = message;
    this.timestamp = timestamp;
  }
  
}
