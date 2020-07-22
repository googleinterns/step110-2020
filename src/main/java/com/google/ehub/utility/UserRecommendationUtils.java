package com.google.ehub.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.PriorityQueue;

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

    return recommendedEmails;
  }

  private Map<String, Integer> getEmailFreqs(Map<Long, List<String>> itemLikes, String userEmail) {
    Map<String, Integer> emailFreqs = new HashMap<String, Integer>();

    for (Map.Entry<Long, List<String>> emailGroup : itemLikes.entrySet()) {
      for (String email : emailGroup.getValue()) {
        emailFreqs.merge(email, 1, (oldFreq, deltaFreq) -> oldFreq + deltaFreq);
      }
    }

    return emailFreqs;
  }

  private PriorityQueue<Map.Entry<String, Integer>> getEmailMinHeap(
      Map<String, Integer> emailFreqs) {
    PriorityQueue<Map.Entry<String, Integer>> emailMinHeap =
        new PriorityQueue<>((a, b) -> Integer.compare(a.getValue(), b.getValue()));

    for (Map.Entry<String, Integer> entry : emailFreqs.entrySet()) {
      emailMinHeap.offer(entry);

      if (emailMinHeap.size() > MAX_NUMBER_OF_RECOMMENDATIONS) {
        emailMinHeap.poll();
      }
    }

    return emailMinHeap;
  }

  private List<String> getRecommendedEmailsInDescendingOrder(
      PriorityQueue<Map.Entry<String, Integer>> emailMinHeap) {
    List<String> recommendedEmails = new ArrayList<String>();

    while (!emailMinHeap.isEmpty()) {
      recommendedEmails.add(emailMinHeap.poll().getKey());
    }

    // The most recommended emails should come first.
    Collections.reverse(recommendedEmails);

    return recommendedEmails;
  }
}
