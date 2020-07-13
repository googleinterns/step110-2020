package com.google.ehub.utility;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UtilsTest {
  @Test
  public void isNextHigherEmptyStringValid() {
    Assert.assertEquals("", Utils.getNextHigherString(""));
  }

  @Test
  public void isNextHigherSingleCharacterValid() {
    Assert.assertEquals("b", Utils.getNextHigherString("a"));
  }

  @Test
  public void isNextHigherFullStringValid() {
    Assert.assertEquals("zzzzza", Utils.getNextHigherString("zzzzz"));
  }

  @Test
  public void isNextHigherRegularStringValid() {
    Assert.assertEquals("helloworle", Utils.getNextHigherString("helloworld"));
  }

  @Test
  public void isNextHigherNullStringValid() {
    Assert.assertEquals("", Utils.getNextHigherString(null));
  }
}
