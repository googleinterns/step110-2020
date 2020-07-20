package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.ehub.utility.Utils;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EntertainmentItemDatastoreTest {
  private static final String ENTERTAINMENT_ITEM_KIND = "entertainmentItem";
  private static final String DISPLAY_TITLE_PROPERTY_KEY = "displayTitle";
  private static final String NORMALIZED_TITLE_PROPERTY_KEY = "normalizedTitle";
  private static final String DESCRIPTION_PROPERTY_KEY = "description";
  private static final String IMAGE_URL_PROPERTY_KEY = "imageUrl";
  private static final String RELEASE_DATE_TIMESTAMP_MILLIS_PROPERTY_KEY =
      "releaseDateTimestampMillis";
  private static final String RUNTIME_PROPERTY_KEY = "runtime";
  private static final String GENRE_PROPERTY_KEY = "genre";
  private static final String DIRECTORS_PROPERTY_KEY = "directors";
  private static final String WRITERS_PROPERTY_KEY = "writers";
  private static final String ACTORS_PROPERTY_KEY = "actors";
  private static final String OMDB_ID_PARAMETER_KEY = "omdbId";
  private static final String NUMBER_OF_LIKES_PROPERTY_KEY = "numberOfLikes";

  private static final String[] TITLES_IN_ASCENDING_ORDER = {
      "Avengers", "Nemo", "Shrek", "Star Wars", "Transformers"};

  private static final String[] RELASE_DATE_IN_DESCENDING_ORDER = {
      "01 Jul 2020", "07 Dec 2018", "15 Jun 2015", "11 Jan 2001", "26 Feb 2000"};

  private static final Long[] NUMBER_OF_LIKES_IN_DESCENDING_ORDER = {22L, 18L, 13L, 11L, 0L};

  private static final String DESCRIPTION = "Blah....";
  private static final String IMAGE_URL = "Image.png";
  private static final String RELEASE_DATE = "25 Dec 2012";
  private static final String RUNTIME = "2 hours";
  private static final String GENRE = "Sci-Fi";
  private static final String DIRECTORS = "George Lucas";
  private static final String WRITERS = "George Lucas";
  private static final String ACTORS = "Mark Hamill, Harrison Ford";
  private static final String OMDB_ID = "tt23113212";

  private static final Long RELEASE_DATE_TIMESTAMP_MILLIS = 1356393600000L;
  private static final Long NUMBER_OF_LIKES = 24L;

  private final EntertainmentItemDatastore entertainmentItemDatastore =
      EntertainmentItemDatastore.getInstance();
  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void init() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void addItemToDatastore_entityGetsAddedWithValidKindAndProperties() {
    entertainmentItemDatastore.addItemToDatastore(new EntertainmentItem.Builder()
                                                      .setTitle(TITLES_IN_ASCENDING_ORDER[0])
                                                      .setDescription(DESCRIPTION)
                                                      .setImageUrl(IMAGE_URL)
                                                      .setReleaseDate(RELEASE_DATE)
                                                      .setRuntime(RUNTIME)
                                                      .setGenre(GENRE)
                                                      .setDirectors(DIRECTORS)
                                                      .setWriters(WRITERS)
                                                      .setActors(ACTORS)
                                                      .setOmdbId(OMDB_ID)
                                                      .setNumberOfLikes(NUMBER_OF_LIKES)
                                                      .build());

    Query query = new Query(ENTERTAINMENT_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);
    List<Entity> entityList = queryResults.asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(1, entityList.size());
    Assert.assertEquals(
        TITLES_IN_ASCENDING_ORDER[0], entityList.get(0).getProperty(DISPLAY_TITLE_PROPERTY_KEY));
    Assert.assertEquals(TITLES_IN_ASCENDING_ORDER[0].toLowerCase(),
        entityList.get(0).getProperty(NORMALIZED_TITLE_PROPERTY_KEY));
    Assert.assertEquals(DESCRIPTION, entityList.get(0).getProperty(DESCRIPTION_PROPERTY_KEY));
    Assert.assertEquals(IMAGE_URL, entityList.get(0).getProperty(IMAGE_URL_PROPERTY_KEY));
    Assert.assertEquals(RELEASE_DATE_TIMESTAMP_MILLIS,
        entityList.get(0).getProperty(RELEASE_DATE_TIMESTAMP_MILLIS_PROPERTY_KEY));
    Assert.assertEquals(RUNTIME, entityList.get(0).getProperty(RUNTIME_PROPERTY_KEY));
    Assert.assertEquals(GENRE, entityList.get(0).getProperty(GENRE_PROPERTY_KEY));
    Assert.assertEquals(DIRECTORS, entityList.get(0).getProperty(DIRECTORS_PROPERTY_KEY));
    Assert.assertEquals(WRITERS, entityList.get(0).getProperty(WRITERS_PROPERTY_KEY));
    Assert.assertEquals(ACTORS, entityList.get(0).getProperty(ACTORS_PROPERTY_KEY));
    Assert.assertEquals(OMDB_ID, entityList.get(0).getProperty(OMDB_ID_PARAMETER_KEY));
    Assert.assertEquals(
        NUMBER_OF_LIKES, entityList.get(0).getProperty(NUMBER_OF_LIKES_PROPERTY_KEY));
  }

  @Test
  public void queryForNonExistentItem_optionalHasNoItem() {
    Optional<EntertainmentItem> item =
        entertainmentItemDatastore.queryItem(/* Non-Existent Id */ 23114121);

    Assert.assertFalse(item.isPresent());
  }

  @Test
  public void queryForExistentItem_optionalHasItem() {
    Entity itemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);

    datastoreService.put(itemEntity);

    Optional<EntertainmentItem> item =
        entertainmentItemDatastore.queryItem(itemEntity.getKey().getId());

    Assert.assertTrue(item.isPresent());
  }

  @Test
  public void queryForNonExistentItems_itemListIsEmpty() {
    EntertainmentItemList itemList =
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults());

    Assert.assertTrue(itemList.getItems().isEmpty());
  }

  @Test
  public void queryForExistentItems_itemListHasAllItems() {
    final int itemsAdded = 15;

    for (int i = 0; i < itemsAdded; i++) {
      datastoreService.put(new Entity(ENTERTAINMENT_ITEM_KIND));
    }

    EntertainmentItemList itemList =
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(itemsAdded, itemList.getItems().size());
  }

  @Test
  public void queryWithAscendingTitlePrefix_itemListIsCorrectlyOrdered() {
    for (int i = 0; i < TITLES_IN_ASCENDING_ORDER.length; i++) {
      Entity entity = new Entity(ENTERTAINMENT_ITEM_KIND);
      entity.setProperty(DISPLAY_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[i]);
      entity.setProperty(NORMALIZED_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[i].toLowerCase());

      datastoreService.put(entity);
    }

    EntertainmentItemList itemList = entertainmentItemDatastore.queryItemsByTitlePrefix(
        FetchOptions.Builder.withDefaults(), /* Empty Search Value */ "", SortDirection.ASCENDING);
    String[] actual = new String[itemList.getItems().size()];

    for (int i = 0; i < actual.length; ++i) {
      actual[i] = itemList.getItems().get(i).getTitle();
    }

    Assert.assertArrayEquals(TITLES_IN_ASCENDING_ORDER, actual);
  }

  @Test
  public void queryWithDescendingTitlePrefix_itemListIsCorrectlyOrdered() {
    for (int i = 0; i < TITLES_IN_ASCENDING_ORDER.length; i++) {
      Entity entity = new Entity(ENTERTAINMENT_ITEM_KIND);
      entity.setProperty(DISPLAY_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[i]);
      entity.setProperty(NORMALIZED_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[i].toLowerCase());

      datastoreService.put(entity);
    }

    EntertainmentItemList itemList = entertainmentItemDatastore.queryItemsByTitlePrefix(
        FetchOptions.Builder.withDefaults(), "S", SortDirection.DESCENDING);
    String[] actual = new String[itemList.getItems().size()];

    for (int i = 0; i < actual.length; i++) {
      actual[i] = itemList.getItems().get(i).getTitle();
    }

    Assert.assertArrayEquals(
        new String[] {TITLES_IN_ASCENDING_ORDER[3], TITLES_IN_ASCENDING_ORDER[2]}, actual);
  }

  @Test
  public void queryWithRecentReleaseDate_itemListIsCorrectlyOrdered() {
    for (int i = 0; i < RELASE_DATE_IN_DESCENDING_ORDER.length; i++) {
      entertainmentItemDatastore.addItemToDatastore(
          new EntertainmentItem.Builder()
              .setReleaseDate(RELASE_DATE_IN_DESCENDING_ORDER[i])
              .build());
    }

    EntertainmentItemList itemList = entertainmentItemDatastore.queryItemsByReleaseDate(
        FetchOptions.Builder.withDefaults(), SortDirection.DESCENDING);
    String[] actual = new String[itemList.getItems().size()];

    for (int i = 0; i < actual.length; i++) {
      actual[i] = itemList.getItems().get(i).getReleaseDate();
    }

    Assert.assertArrayEquals(RELASE_DATE_IN_DESCENDING_ORDER, actual);
  }

  @Test
  public void queryWithMostPopularItems_itemListIsCorrectlyOrdered() {
    for (int i = 0; i < NUMBER_OF_LIKES_IN_DESCENDING_ORDER.length; i++) {
      entertainmentItemDatastore.addItemToDatastore(
          new EntertainmentItem.Builder()
              .setNumberOfLikes(NUMBER_OF_LIKES_IN_DESCENDING_ORDER[i])
              .build());
    }

    EntertainmentItemList itemList = entertainmentItemDatastore.queryItemsByPopularity(
        FetchOptions.Builder.withDefaults(), SortDirection.DESCENDING);
    Long[] actual = new Long[itemList.getItems().size()];

    for (int i = 0; i < actual.length; i++) {
      actual[i] = itemList.getItems().get(i).getNumberOfLikes();
    }

    Assert.assertArrayEquals(NUMBER_OF_LIKES_IN_DESCENDING_ORDER, actual);
  }
}
