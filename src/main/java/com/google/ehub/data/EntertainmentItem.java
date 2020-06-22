package com.google.ehub.data;

/**
 * Holds information for Entertainment Items used in Datastore.
 */
public class EntertainmentItem {
  private String title;
  private String description;
  private String imageURL;

  public EntertainmentItem(String title, String description, String imageURL) {
    this.title = title;
    this.description = description;
    this.imageURL = imageURL;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getImageURL() {
    return imageURL;
  }
}
