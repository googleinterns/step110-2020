package com.google.ehub.utility;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DatastoreUtilsTest {
  @Test
  public void isNextHigherEmptyStringValid() {
    Assert.assertEquals("", DatastoreUtils.getNextHigherString(""));
  }

  @Test
  public void isNextHigherSingleCharacterValid() {
    Assert.assertEquals("b", DatastoreUtils.getNextHigherString("a"));
  }

  @Test
  public void isNextHigherFullStringValid() {
    Assert.assertEquals("zzzzza", DatastoreUtils.getNextHigherString("zzzzz"));
  }

  @Test
  public void isNextHigherRegularStringValid() {
    Assert.assertEquals("helloworle", DatastoreUtils.getNextHigherString("helloworld"));
  }

  @Test
  public void isNextHigherNullStringValid() {
    Assert.assertEquals("", DatastoreUtils.getNextHigherString(null));
  }
}
