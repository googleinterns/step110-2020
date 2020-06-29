package com.google.sps.servlets;

public class UserProfile {
  private final String username;
  private final String bio;
  private final String email;
  private final String name;

  public UserProfile(String name, String username, String bio, String email) {
    this.name = name;
    this.username = username;
    this.bio = bio;
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public String getBio() {
    return bio;
  }

  public String getEmail() {
    return email;
  }
}
