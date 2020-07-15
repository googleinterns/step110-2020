package com.google.ehub.utility;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UtilsTest {
  private static final String DATE_FORMAT = "dd/MM/yyyy";
  private static final String INVALID_DATE_FORMAT = "dddmmmmdczyyy";
  private static final String VALID_DATE = "23/08/2013";
  private static final String DATE_WITH_CONFLICTING_FORMAT = "23 Jul 2013";

  private static final Long CORRECT_TIMESTAMP = 1377216000000L;

  @Test
  public void getNextHigherStringWithEmptyParam_ReturnsEmptyString() {
    Assert.assertEquals("", Utils.getNextHigherString(""));
  }

  @Test
  public void getNextHigherStringWithSingleLetterParam_ReturnsNextLetter() {
    Assert.assertEquals("b", Utils.getNextHigherString("a"));
  }

  @Test
  public void getNextHigherStringWithCappedParam_ReturnsCappedParamWithNewAppendedLetter() {
    Assert.assertEquals("zzzzza", Utils.getNextHigherString("zzzzz"));
  }

  @Test
  public void getNextHigherStringWithDefaultValidParam_ReturnsParamWithIncreasedLastLetter() {
    Assert.assertEquals("helloworle", Utils.getNextHigherString("helloworld"));
  }

  @Test
  public void getNextHigherStringWithNullParam_ReturnsEmptyString() {
    Assert.assertEquals("", Utils.getNextHigherString(null));
  }

  @Test
  public void getTimestampMillisFromDateWithNullDate_ReturnsNull() {
    Assert.assertNull(Utils.getTimestampMillisFromDate(null, DATE_FORMAT));
  }

  @Test
  public void getTimestampMillisFromDateWithNullFormat_ReturnsNull() {
    Assert.assertNull(Utils.getTimestampMillisFromDate(VALID_DATE, null));
  }

  @Test
  public void getTimestampMillisFromDateWithValidParams_ReturnsCorrectTimestamp() {
    Assert.assertEquals(
        CORRECT_TIMESTAMP, Utils.getTimestampMillisFromDate(VALID_DATE, DATE_FORMAT));
  }

  @Test
  public void getTimestampMillisFromDateWithConflictingFormat_ReturnsNull() {
    Assert.assertNull(Utils.getTimestampMillisFromDate(DATE_WITH_CONFLICTING_FORMAT, DATE_FORMAT));
  }

  @Test
  public void getTimestampMillisFromDateWithInvalidFormat_ReturnsNull() {
    Assert.assertNull(Utils.getTimestampMillisFromDate(VALID_DATE, INVALID_DATE_FORMAT));
  }

  @Test
  public void getDateFromTimestampMillisWithNullTimestampMillis_ReturnsNull() {
    Assert.assertNull(Utils.getDateFromTimestampMillis(null, DATE_FORMAT));
  }

  @Test
  public void getDateFromTimestampMillisWithNullFormat_ReturnsNull() {
    Assert.assertNull(Utils.getDateFromTimestampMillis(CORRECT_TIMESTAMP, null));
  }

  @Test
  public void getDateFromTimestampMillisWithValidParams_ReturnsCorrectDate() {
    Assert.assertEquals(
        VALID_DATE, Utils.getDateFromTimestampMillis(CORRECT_TIMESTAMP, DATE_FORMAT));
  }

  @Test
  public void getDateFromTimestampMillisWithInvalidFormat_ReturnsNull() {
    Assert.assertNull(Utils.getDateFromTimestampMillis(CORRECT_TIMESTAMP, INVALID_DATE_FORMAT));
  }
}
