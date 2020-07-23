package com.google.ehub.data;

import java.util.List;

/**
 * Holds user information used to create JSON object for Profile Servlet's Get request response.
 */
public final class UserData {
  private final UserProfile profile;
  private final List<String> recommendedUsers;

  public UserData(UserProfile profile, List<String> recommendedUsers) {
    this.profile = profile;
    this.recommendedUsers = recommendedUsers;
  }

  public UserProfile getProfile() {
    return profile;
  }

  public List<String> getRecommendedUsers() {
    return recommendedUsers;
  }
}
