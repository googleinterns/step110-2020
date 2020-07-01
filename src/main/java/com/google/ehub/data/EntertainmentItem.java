package com.google.ehub.data;

/**
 * Holds information for Entertainment Items used in Datastore.
 */
public final class EntertainmentItem {
  private final long uniqueId;
  private final String title;
  private final String description;
  private final String imageUrl;

  public EntertainmentItem(long uniqueId, String title, String description, String imageUrl) {
    this.uniqueId = uniqueId;
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  public long getUniqueId() {
    return uniqueId;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
