package com.google.ehub.utility;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UserRecommendationUtilsTest {
  private static final int MAX_RECOMMENDED_USERS = 10;

  private final UserRecommendationUtils recommendationUtils = new UserRecommendationUtils();

  @Test
  public void getRecommendedEmailsFromEmptyList_returnsEmptyList() {
    Assert.assertTrue(recommendationUtils.getRecommendedEmails(Maps.newHashMap()).isEmpty());
  }

  @Test
  public void getRecommendedEmailsFromSingleUserList_returnsListWithSingleUser() {
    Map<Long, Set<String>> itemLikes = ImmutableMap.of(1L, Sets.newHashSet("Bryan"));

    Assert.assertEquals(
        Arrays.asList("Bryan"), recommendationUtils.getRecommendedEmails(itemLikes));
  }

  @Test
  public void getRecommendedEmailsFromMultipleItemList_returnsListWithCorrectItems() {
    Map<Long, Set<String>> itemLikes = ImmutableMap.of(1L, Sets.newHashSet("Bryan", "Oyin"), 2L,
        Sets.newHashSet("Bryan", "Oyin", "Eric"), 3L, Sets.newHashSet("Bryan"));

    Assert.assertEquals(Arrays.asList("Bryan", "Oyin", "Eric"),
        recommendationUtils.getRecommendedEmails(itemLikes));
  }

  @Test
  public void
  getRecommendedEmailsWithTies_returnsListWithCorrectItemsInAscendingLexicographicOrderInCaseOfTies() {
    Map<Long, Set<String>> itemLikes =
        ImmutableMap.of(1L, Sets.newHashSet("Bryan", "Oyin", "Eric", "Jessica", "Rodrigo"));

    Assert.assertEquals(Arrays.asList("Bryan", "Eric", "Jessica", "Oyin", "Rodrigo"),
        recommendationUtils.getRecommendedEmails(itemLikes));
  }

  @Test
  public void
  getRecommendedEmailsFromListWithMoreThanMaxRecommendedUsers_returnsListWithMaxRecommendedUsers() {
    Map<Long, Set<String>> itemLikes = ImmutableMap.of(1L, Sets.newHashSet("Cat", "Dog", "Fish"),
        2L, Sets.newHashSet("Cat", "Dog", "Lion"), 3L,
        Sets.newHashSet("Horse", "Dinosaur", "Shark", "Tiger"), 4L,
        Sets.newHashSet("Dinosaur", "Shark", "Owl", "Ant", "Eagle"), 5L,
        Sets.newHashSet("Ant", "Dinosaur", "Eagle", "Dog"));

    Assert.assertEquals(Arrays.asList("Dinosaur", "Dog", "Ant", "Cat", "Eagle", "Shark", "Fish",
                            "Horse", "Lion", "Owl"),
        recommendationUtils.getRecommendedEmails(itemLikes));
  }
}
