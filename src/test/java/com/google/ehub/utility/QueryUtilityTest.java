package com.google.ehub.utility;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class QueryUtilityTest {
  @Test
  public void isNextHigherEmptyStringValid() {
    Assert.assertEquals("", QueryUtility.getNextHigherString(""));
  }

  @Test
  public void isNextHigherSingleCharacterValid() {
    Assert.assertEquals("b", QueryUtility.getNextHigherString("a"));
  }

  @Test
  public void isNextHigherFullStringValid() {
    Assert.assertEquals("zzzzza", QueryUtility.getNextHigherString("zzzzz"));
  }

  @Test
  public void isNextHigherRegularStringValid() {
    Assert.assertEquals("helloworle", QueryUtility.getNextHigherString("helloworld"));
  }

  @Test
  public void isNextHigherNullStringValid() {
    Assert.assertEquals("", QueryUtility.getNextHigherString(null));
  }
}
