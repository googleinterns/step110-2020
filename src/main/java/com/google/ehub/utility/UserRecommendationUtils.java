package com.google.ehub.utility;

import com.google.ehub.data.FavoriteItemDatastore;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that implements the algorithm used to recommend the users that have the most common
 * number of shared likes.
 */
public final class UserRecommendationUtils {
  private static final FavoriteItemDatastore favoriteItemDatastore =
      FavoriteItemDatastore.getInstance();

  /**
   * Finds the most recommended emails up to a given limit.
   *
   * @param userEmail the email of the user that wants to search for its recommended email list
   * @param maxNumberOfRecommendations the maximum desired size for the recommendation list
   * @return list containing the most recommended emails
   */
  public static List<String> getRecommendedEmails(
      String userEmail, int maxNumberOfRecommendations) {
    List<Long> itemIdsLikedByUser = favoriteItemDatastore.queryFavoriteIds(userEmail);
    List<List<String>> emailsThatLikedSameItem =
        getEmailsThatLikeSameItemsAsUser(itemIdsLikedByUser);

    return getRecommendedEmails(emailsThatLikedSameItem, maxNumberOfRecommendations);
  }

  private static List<String> getRecommendedEmails(
      List<List<String>> emailsThatLikedSameItem, int maxNumberOfRecommendations) {
    List<String> recommendedEmails = new ArrayList<String>();

    // TODO: Implement algorithm that finds the users that have the highest amount of common items,
    // could be implemented
    // by using a Map<String, int> storing counts and a priority queue to get the
    // "maxNumberOfRecommendations"

    return recommendedEmails;
  }

  private static List<List<String>> getEmailsThatLikeSameItemsAsUser(
      List<Long> itemIdsLikedByUser) {
    List<List<String>> emailsPerLikedItem = new ArrayList<>();

    for (Long itemId : itemIdsLikedByUser) {
      emailsPerLikedItem.add(favoriteItemDatastore.queryEmails(itemId));
    }

    return emailsPerLikedItem;
  }
}
