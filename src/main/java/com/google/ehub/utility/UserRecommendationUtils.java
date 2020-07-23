package com.google.ehub.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class that implements the algorithm used to recommend the users that have the most common
 * number of shared likes.
 */
public final class UserRecommendationUtils {
  private static final int MAX_NUMBER_OF_RECOMMENDATIONS = 10;

  /**
   * Finds the most recommended emails up to a maximum of ten recommendations in descending order.
   *
   * @param itemLikes map with the key representing an itemId and the value representing the emails
   *     that liked that item
   * @return list containing the most recommended emails in descending order
   */
  public List<String> getRecommendedEmails(Map<Long, Set<String>> itemLikes) {
    List<String> recommendedEmails = new ArrayList<String>();

    // TODO: Implement algorithm that finds the users that have the highest amount of common items,
    // could be implemented
    // by using a Map<String, int> storing counts and a priority queue to get the
    // "maxNumberOfRecommendations"

    return recommendedEmails;
  }
}
