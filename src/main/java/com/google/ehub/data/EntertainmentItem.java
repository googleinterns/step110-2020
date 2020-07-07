package com.google.ehub.data;

import java.util.Optional;

/**
 * Holds information for Entertainment Items used in Datastore.
 */
public final class EntertainmentItem {
  /*
   * uniqueId is optional because it is sometimes necessary to create this object with an unassigned
   * Id. This Id gets added after Datastore generates a unique Key.
   */
  private final Optional<Long> uniqueId;

  private final String title;
  private final String description;
  private final String imageUrl;

  public EntertainmentItem(
      Optional<Long> uniqueId, String title, String description, String imageUrl) {
    this.uniqueId = uniqueId;
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  public Optional<Long> getUniqueId() {
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
