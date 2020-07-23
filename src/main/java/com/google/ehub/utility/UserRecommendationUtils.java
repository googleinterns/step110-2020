package com.google.ehub.utility;

import com.google.common.collect.MinMaxPriorityQueue;
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
   * In the case of a tie, the order will use increasing lexicographical order.
   *
   * @param itemLikes map with the key representing an itemId and the value representing the emails
   *     that liked that item
   * @return list containing the most recommended emails in descending order
   */
  public List<String> getRecommendedEmails(Map<Long, Set<String>> itemLikes) {
    return getRecommendedEmailsInDescendingOrder(getEmailPriorityQueue(getEmailFreqs(itemLikes)));
  }

  private Map<String, Integer> getEmailFreqs(Map<Long, Set<String>> itemLikes) {
    Map<String, Integer> emailFreqs = new HashMap<String, Integer>();

    for (Map.Entry<Long, Set<String>> emailGroup : itemLikes.entrySet()) {
      for (String email : emailGroup.getValue()) {
        emailFreqs.merge(email, 1, (oldFreq, deltaFreq) -> oldFreq + deltaFreq);
      }
    }

    return emailFreqs;
  }

  private MinMaxPriorityQueue<Map.Entry<String, Integer>> getEmailPriorityQueue(
      Map<String, Integer> emailFreqs) {
    MinMaxPriorityQueue<Map.Entry<String, Integer>> emailPriorityQueue =
        MinMaxPriorityQueue
            .orderedBy((Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) -> {
              // If the counts are tied, then compare emails lexicographically.
              if (a.getValue() == b.getValue()) {
                return a.getKey().compareTo(b.getKey());
              }

              return Integer.compare(b.getValue(), a.getValue());
            })
            .maximumSize(MAX_NUMBER_OF_RECOMMENDATIONS)
            .create();

    emailPriorityQueue.addAll(emailFreqs.entrySet());

    return emailPriorityQueue;
  }

  private List<String> getRecommendedEmailsInDescendingOrder(
      MinMaxPriorityQueue<Map.Entry<String, Integer>> emailPriorityQueue) {
    List<String> recommendedEmails = new ArrayList<String>();

    while (!emailPriorityQueue.isEmpty()) {
      recommendedEmails.add(emailPriorityQueue.poll().getKey());
    }

    return recommendedEmails;
  }
}
