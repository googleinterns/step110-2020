package com.google.ehub.data;

public class CommentData {
  private final String message;
  private final Long timestamp;

  public CommentData(String message, long timestamp){
    this.message = message;
    this.timestamp = timestamp;
  }
  
}
