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
  private final String releaseDate;
  private final String runtime;
  private final String genre;
  private final String directors;
  private final String writers;
  private final String actors;

  public EntertainmentItem(Optional<Long> uniqueId, String title, String description,
      String imageUrl, String releaseDate, String runtime, String genre, String directors,
      String writers, String actors) {
    this.uniqueId = uniqueId;
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
    this.releaseDate = releaseDate;
    this.runtime = runtime;
    this.genre = genre;
    this.directors = directors;
    this.writers = writers;
    this.actors = actors;
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

  public String getReleaseDate() {
    return releaseDate;
  }

  public String getRuntime() {
    return runtime;
  }

  public String getGenre() {
    return genre;
  }

  public String getDirectors() {
    return directors;
  }

  public String getWriters() {
    return writers;
  }

  public String getActors() {
    return actors;
  }
}
