package com.google.ehub.data;

import com.google.ehub.data.EntertainmentItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that defines an ItemPageData object
 */
public class ItemPageData {
  private final EntertainmentItem item;
  private final List<CommentData> comments;
  private final Boolean loginStatus;

  public ItemPageData(EntertainmentItem item, List<CommentData> comments, Boolean loginStatus) {
    this.item = item;
    this.comments = comments;
    this.loginStatus = loginStatus;
  }

  public EntertainmentItem getItem() {
    return item;
  }

  public List<CommentData> getComments() {
    return comments;
  }

  public Boolean getLoginStatus() {
    return loginStatus;
  }
}
