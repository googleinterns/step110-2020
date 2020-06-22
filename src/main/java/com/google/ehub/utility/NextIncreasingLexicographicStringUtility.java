package com.google.ehub.utility;

/**
 * Utility class that holds method to find the next lexicographic string.
 */
public final class NextIncreasingLexicographicStringUtility {
  /**
   * Finds the next increasing lexicographic string.
   *
   * @param str the string used to find the next increasing lexicographic string
   * @return the next lexicographic string in increasing order
   */
  public static String getNextIncreasingLexicographicString(String str) {
    if (str == null) {
      return "";
    }
    if (str.isEmpty()) {
      return "a";
    }

    int i = str.length() - 1;

    while (i >= 0 && str.charAt(i) == 'z') {
      --i;
    }

    if (i == -1) {
      str += 'a';
    } else {
      str = str.substring(0, i) + (char) ((int) str.charAt(i) + 1) + str.substring(i + 1);
    }

    return str;
  }

  private NextIncreasingLexicographicStringUtility() {}
}
