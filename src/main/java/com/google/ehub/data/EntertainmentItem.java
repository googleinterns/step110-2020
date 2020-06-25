package com.google.ehub.data;

import java.util.UUID;

/**
 * Holds information for Entertainment Items used in Datastore.
 */
public final class EntertainmentItem {
  private final long uniqueID;
  private final String title;
  private final String description;
  private final String imageURL;

  /**
   * Creates EntertainmentItem with a new unique ID.
   *
   * @param title the title used to construct the EntertainmentItem
   * @param description the description used to construct the EntertainmentItem
   * @param imageURL the image URL used to contruct the EntertainmentItem
   * @return new EntertainmentItem with unique ID
   */
  public static EntertainmentItem unassignedItem(
      String title, String description, String imageURL) {
    return new EntertainmentItem(
        UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE, title, description, imageURL);
  }

  public EntertainmentItem(long uniqueID, String title, String description, String imageURL) {
    this.uniqueID = uniqueID;
    this.title = title;
    this.description = description;
    this.imageURL = imageURL;
  }

  public long getUniqueID() {
    return uniqueID;
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
