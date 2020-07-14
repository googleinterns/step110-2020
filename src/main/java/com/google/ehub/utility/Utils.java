package com.google.ehub.utility;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Utility class holding miscellaneous methods used across the codebase.
 */
public final class Utils {
  private Utils() {}

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

  /**
   * Gets the millisecond timestamp of a given date in a specific format.
   *
   * @param date the date to convert into a timestamp eg. ("04-23-2020")
   * @param format the format used by the given date eg. ("MM-DD-YYYY")
   * @return the timestamp of the date in milliseconds, or null if it failed to parse
   */
  public static Long getTimestampMillisFromDate(String date, String format) {
    SimpleDateFormat dateFormatter;

    try {
      dateFormatter = new SimpleDateFormat(format);
    } catch (NullPointerException e) {
      System.err.println("Date format can't be null: " + e);
      return null;
    } catch (IllegalArgumentException e) {
      System.err.println("Date formatter couldn't be created with given arguments: " + e);
      return null;
    }

    Date parsedDate;

    try {
      parsedDate = dateFormatter.parse(date);
    } catch (NullPointerException e) {
      System.err.println("Date can't be null: " + e);
      return null;
    } catch (ParseException e) {
      System.err.println("Failed to parse date: " + e);
      return null;
    }

    return parsedDate.getTime();
  }

  /**
   * Gets the date with a specific format from a millisecond timestamp.
   *
   * @param timestampMillis the millisecond timestamp to get the date from
   * @param format the format used to write the date on the string
   * @return the date as a string with the format that was given
   */
  public static String getDateFromTimestampMillis(Long timestampMillis, String format) {
    Date date;

    try {
      date = new Date(timestampMillis);
    } catch (NullPointerException e) {
      System.err.println("Date could not be created: " + e);
      return null;
    }

    SimpleDateFormat dateFormatter;

    try {
      dateFormatter = new SimpleDateFormat(format);
    } catch (NullPointerException e) {
      System.err.println("Date format can't be null: " + e);
      return null;
    } catch (IllegalArgumentException e) {
      System.err.println("Date formatter couldn't be created with given arguments: " + e);
      return null;
    }

    return dateFormatter.format(date);
  }
}
