package com.google.ehub.data;

import java.util.Optional;

public final class EntertainmentItemBuilder {
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

  public EntertainmentItemBuilder setUniqueId(Optional<Long> uniqueId) {
    this.uniqueId = uniqueId;
    return this;
  }

  public EntertainmentItemBuilder setTitle(String title) {
    this.title = title;
    return this;
  }

  public EntertainmentItemBuilder setDescription(String description) {
    this.description = description;
    return this;
  }

  public EntertainmentItemBuilder setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

  public EntertainmentItemBuilder setReleaseDate(String releaseDate) {
    this.releaseDate = releaseDate;
    return this;
  }

  public EntertainmentItemBuilder setRuntime(String runtime) {
    this.runtime = runtime;
    return this;
  }

  public EntertainmentItemBuilder setGenre(String genre) {
    this.genre = genre;
    return this;
  }

  public EntertainmentItemBuilder setDirectors(String directors) {
    this.directors = directors;
    return this;
  }

  public EntertainmentItemBuilder setWriters(String writers) {
    this.writers = writers;
    return this;
  }

  public EntertainmentItemBuilder setActors(String actors) {
    this.actors = actors;
    return this;
  }
}
