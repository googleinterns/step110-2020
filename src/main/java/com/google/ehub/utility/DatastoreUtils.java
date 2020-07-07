package com.google.ehub.utility;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/**
 * Utility class with useful methods used in Datastore.
 */
public final class DatastoreUtils {
  private DatastoreUtils() {}

  /**
   * Creates the filter used for prefix searches in Datastore.
   *
   * @param prefixProperty the name of the property used to filter by the query
   * @param normalizedPrefix the prefix value used to search by the filter
   * @return filter operator used for prefix search in Datastore
   */
  public static Filter getPrefixFilter(String prefixProperty, String normalizedPrefix) {
    return CompositeFilterOperator.and(
        new FilterPredicate(prefixProperty, FilterOperator.GREATER_THAN_OR_EQUAL, normalizedPrefix),
        new FilterPredicate(
            prefixProperty, FilterOperator.LESS_THAN, getNextHigherString(normalizedPrefix)));
  }

  /**
   * Finds the next higher lexicographic string.
   * Ex: "a" => "b", "yellow" => "yellox", "zzz" => "zzza"
   *
   * @param str the string used to find the next higher lexicographic string
   * @return the next lexicographic string in increasing order, if the string is empty or null then
   *     an empty string will be returned
   */
  public static String getNextHigherString(String str) {
    if (str == null || str.isEmpty()) {
      return "";
    }

    int lastNonMaxCharIndex = str.length() - 1;

    // TODO: Handle non-english chars.
    while (lastNonMaxCharIndex >= 0 && str.charAt(lastNonMaxCharIndex) == 'z') {
      --lastNonMaxCharIndex;
    }

    if (lastNonMaxCharIndex == -1) {
      // The whole string is filled with max chars, so adding a new min char yields the next higher
      // string eg. "zzzz" < "zzzza"
      return str + 'a';
    } else {
      // Increasing the last non-max char yields the next higher string eg. "abbbzzz" < "abbczzz"
      char nextHigherPivotChar = (char) ((int) str.charAt(lastNonMaxCharIndex) + 1);
      // Next higher string shares same chars as original one except for the pivot char that got
      // increased.
      return str.substring(0, lastNonMaxCharIndex) + nextHigherPivotChar
          + str.substring(lastNonMaxCharIndex + 1);
    }
  }
}
