package com.google.ehub.utility;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class NextIncreasingLexicographicStringUtilityTest {
  @Test
  public void isEmptyStringValid() {
    Assert.assertEquals(
        "a", NextIncreasingLexicographicStringUtility.getNextIncreasingLexicographicString(""));
  }

  @Test
  public void isSingleCharacterValid() {
    Assert.assertEquals(
        "b", NextIncreasingLexicographicStringUtility.getNextIncreasingLexicographicString("a"));
  }

  @Test
  public void isFullStringValid() {
    Assert.assertEquals("zzzzza",
        NextIncreasingLexicographicStringUtility.getNextIncreasingLexicographicString("zzzzz"));
  }

  @Test
  public void isRegularStringValid() {
    Assert.assertEquals("helloworle",
        NextIncreasingLexicographicStringUtility.getNextIncreasingLexicographicString(
            "helloworld"));
  }

  @Test
  public void isNullStringValid() {
    Assert.assertEquals(
        "", NextIncreasingLexicographicStringUtility.getNextIncreasingLexicographicString(null));
  }
}
