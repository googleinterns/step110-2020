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

  private static final String[] TITLES_IN_ASCENDING_ORDER = {
      "Avengers", "Nemo", "Shrek", "Star Wars", "Transformers"};

  private static final String DESCRIPTION = "Blah....";
  private static final String IMAGE_URL = "Image.png";

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
  public void addItemToDatastore_EntityGetsAddedWithValidKindAndProperties() {
    entertainmentItemDatastore.addItemToDatastore(
        TITLES_IN_ASCENDING_ORDER[0], DESCRIPTION, IMAGE_URL);

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
  }

  @Test
  public void queryForNonExistentItem_OptionalHasNoItem() {
    Optional<EntertainmentItem> item =
        entertainmentItemDatastore.queryItem(/* Non-Existent Id */ 23114121);

    Assert.assertFalse(item.isPresent());
  }

  @Test
  public void queryForExistentItem_OptionalHasItem() {
    Entity itemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);
    itemEntity.setProperty(DISPLAY_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[0]);
    itemEntity.setProperty(
        NORMALIZED_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[0].toLowerCase());
    itemEntity.setProperty(DESCRIPTION_PROPERTY_KEY, DESCRIPTION);
    itemEntity.setProperty(IMAGE_URL_PROPERTY_KEY, IMAGE_URL);

    datastoreService.put(itemEntity);

    Optional<EntertainmentItem> item =
        entertainmentItemDatastore.queryItem(itemEntity.getKey().getId());

    Assert.assertTrue(item.isPresent());
  }

  @Test
  public void queryForNonExistentItems_ItemListIsEmpty() {
    List<EntertainmentItem> itemList = entertainmentItemDatastore.queryAllItems();

    Assert.assertTrue(itemList.isEmpty());
  }

  @Test
  public void queryForExistentItems_ItemListHasAllItems() {
    final int itemsAdded = 15;

    for (int i = 0; i < itemsAdded; i++) {
      datastoreService.put(new Entity(ENTERTAINMENT_ITEM_KIND));
    }

    List<EntertainmentItem> itemList = entertainmentItemDatastore.queryAllItems();

    Assert.assertEquals(itemsAdded, itemList.size());
  }

  @Test
  public void queryWithAlphabeticalTitleOrder_ItemListIsCorrectlyOrdered() {
    for (int i = 0; i < TITLES_IN_ASCENDING_ORDER.length; i++) {
      Entity entity = new Entity(ENTERTAINMENT_ITEM_KIND);
      entity.setProperty(DISPLAY_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[i]);
      entity.setProperty(NORMALIZED_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[i].toLowerCase());

      datastoreService.put(entity);
    }

    List<EntertainmentItem> itemList =
        entertainmentItemDatastore.queryAllItemsWithTitleOrder(SortDirection.ASCENDING);
    String[] actual = new String[itemList.size()];

    for (int i = 0; i < itemList.size(); ++i) {
      actual[i] = itemList.get(i).getTitle();
    }

    Assert.assertArrayEquals(TITLES_IN_ASCENDING_ORDER, actual);
  }

  @Test
  public void queryWithDecreasingAlphabeticalOrderAndTitlePrefix_ItemListIsCorrectlySelected() {
    for (int i = 0; i < TITLES_IN_ASCENDING_ORDER.length; i++) {
      Entity entity = new Entity(ENTERTAINMENT_ITEM_KIND);
      entity.setProperty(DISPLAY_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[i]);
      entity.setProperty(NORMALIZED_TITLE_PROPERTY_KEY, TITLES_IN_ASCENDING_ORDER[i].toLowerCase());

      datastoreService.put(entity);
    }

    List<EntertainmentItem> itemList =
        entertainmentItemDatastore.queryItemsByTitlePrefix("S", SortDirection.DESCENDING);
    String[] actual = new String[itemList.size()];

    for (int i = 0; i < itemList.size(); ++i) {
      actual[i] = itemList.get(i).getTitle();
    }

    Assert.assertArrayEquals(
        new String[] {TITLES_IN_ASCENDING_ORDER[3], TITLES_IN_ASCENDING_ORDER[2]}, actual);
  }
}
