package com.google.ehub.utility;

import com.google.ehub.data.FavoriteItemDatastore;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that implements the algorithm used to recommend the users that have the most common
 * number of shared likes.
 */
public final class UserRecommendationUtils {
  private static final int MAX_NUMBER_OF_RECOMMENDATIONS = 10;
  private static final FavoriteItemDatastore favoriteItemDatastore =
      FavoriteItemDatastore.getInstance();

  /**
   * Finds the most recommended emails up to a given limit.
   *
   * @param userEmail the email of the user that wants to search for its recommended email list
   * @return list containing the most recommended emails
   */
  public List<String> getRecommendedEmails(
      String userEmail) {
    List<Long> itemIdsLikedByUser = favoriteItemDatastore.queryFavoriteIds(userEmail);
    List<List<String>> emailsThatLikedSameItem =
        getEmailsThatLikeSameItemsAsUser(itemIdsLikedByUser);

    return getRecommendedEmailsFromEmailList(emailsThatLikedSameItem);
  }

  private List<String> getRecommendedEmailsFromEmailList(
      List<List<String>> emailsThatLikedSameItem) {
    List<String> recommendedEmails = new ArrayList<String>();

    // TODO: Implement algorithm that finds the users that have the highest amount of common items,
    // could be implemented
    // by using a Map<String, int> storing counts and a priority queue to get the
    // "maxNumberOfRecommendations"

    return recommendedEmails;
  }

  private List<List<String>> getEmailsThatLikeSameItemsAsUser(
      List<Long> itemIdsLikedByUser) {
    List<List<String>> emailsThatLikedSameItem = new ArrayList<>();

    for (Long itemId : itemIdsLikedByUser) {
      emailsThatLikedSameItem.add(favoriteItemDatastore.queryEmails(itemId));
    }

    return emailsThatLikedSameItem;
  }
}
