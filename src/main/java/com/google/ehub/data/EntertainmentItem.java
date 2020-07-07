package com.google.ehub.data;

import java.util.Optional;

/**
 * Holds information for Entertainment Items used in Datastore.
 */
public final class EntertainmentItem {
  private EntertainmentItem(Optional<Long> uniqueId, String title, String description,
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

  public static final class Builder {
    private Optional<Long> uniqueId = Optional.empty();
    private String title = "";
    private String description = "";
    private String imageUrl = "";
    private String releaseDate = "";
    private String runtime = "";
    private String genre = "";
    private String directors = "";
    private String writers = "";
    private String actors = "";

    public EntertainmentItem build() {
      return new EntertainmentItem(uniqueId, title, description, imageUrl, releaseDate, runtime,
          genre, directors, writers, actors);
    }

    public Builder setUniqueId(Optional<Long> uniqueId) {
      this.uniqueId = uniqueId;
      return this;
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public Builder setReleaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }

    public Builder setRuntime(String runtime) {
      this.runtime = runtime;
      return this;
    }

    public Builder setGenre(String genre) {
      this.genre = genre;
      return this;
    }

    public Builder setDirectors(String directors) {
      this.directors = directors;
      return this;
    }

    public Builder setWriters(String writers) {
      this.writers = writers;
      return this;
    }

    public Builder setActors(String actors) {
      this.actors = actors;
      return this;
    }
  }
}
