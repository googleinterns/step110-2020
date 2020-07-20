package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FavoriteItemDatastoreTest {
  private static final String FAVORITE_ITEM_KIND = "favoriteItem";
  private static final String ENTERTAINMENT_ITEM_KIND = "entertainmentItem";
  private static final String USER_EMAIL_PROPERTY_KEY = "userEmail";
  private static final String ITEM_ID_PROPERTY_KEY = "itemId";
  private static final String NUMBER_OF_LIKES_PROPERTY_KEY = "numberOfLikes";

  private static final String USER_EMAIL = "bryan@gmail.com";
  private static final Long ITEM_ID = 1L;
  private static final Long NUMBER_OF_LIKES = 5L;

  private final FavoriteItemDatastore favoriteItemDatastore = FavoriteItemDatastore.getInstance();
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
  public void addFavoriteItemToDatastore_entityGetsAddedWithValidKindAndProperties() {
    Entity itemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);
    itemEntity.setProperty(NUMBER_OF_LIKES_PROPERTY_KEY, NUMBER_OF_LIKES);

    datastoreService.put(itemEntity);

    final Long itemId = itemEntity.getKey().getId();

    favoriteItemDatastore.addFavoriteItem(USER_EMAIL, itemId);

    Query query = new Query(FAVORITE_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);
    List<Entity> entityList = queryResults.asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(1, entityList.size());
    Assert.assertEquals(USER_EMAIL, entityList.get(0).getProperty(USER_EMAIL_PROPERTY_KEY));
    Assert.assertEquals(itemId, entityList.get(0).getProperty(ITEM_ID_PROPERTY_KEY));

    Optional<EntertainmentItem> entertainmentItem = entertainmentItemDatastore.queryItem(itemId);

    Assert.assertTrue(entertainmentItem.isPresent());
    Assert.assertEquals(
        Long.valueOf(NUMBER_OF_LIKES + 1), entertainmentItem.get().getNumberOfLikes());
  }

  @Test
  public void addFavoriteItemToDatastoreWithDuplicateItems_onlyOneEntityGetsAdded() {
    Entity itemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);
    itemEntity.setProperty(NUMBER_OF_LIKES_PROPERTY_KEY, NUMBER_OF_LIKES);

    datastoreService.put(itemEntity);

    final Long itemId = itemEntity.getKey().getId();

    Entity favoriteItemEntity = new Entity(FAVORITE_ITEM_KIND);
    favoriteItemEntity.setProperty(USER_EMAIL_PROPERTY_KEY, USER_EMAIL);
    favoriteItemEntity.setProperty(ITEM_ID_PROPERTY_KEY, itemId);

    datastoreService.put(favoriteItemEntity);

    favoriteItemDatastore.addFavoriteItem(USER_EMAIL, itemId);

    Query query = new Query(FAVORITE_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);
    List<Entity> entityList = queryResults.asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(1, entityList.size());

    Optional<EntertainmentItem> entertainmentItem = entertainmentItemDatastore.queryItem(itemId);

    Assert.assertTrue(entertainmentItem.isPresent());
    Assert.assertEquals(Long.valueOf(NUMBER_OF_LIKES), entertainmentItem.get().getNumberOfLikes());
  }

  @Test
  public void removeFavoriteItemFromDatastore_entityGetsDeleted() {
    Entity itemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);
    itemEntity.setProperty(NUMBER_OF_LIKES_PROPERTY_KEY, NUMBER_OF_LIKES);

    datastoreService.put(itemEntity);

    final Long itemId = itemEntity.getKey().getId();

    Entity favoriteItemEntity = new Entity(FAVORITE_ITEM_KIND);
    favoriteItemEntity.setProperty(USER_EMAIL_PROPERTY_KEY, USER_EMAIL);
    favoriteItemEntity.setProperty(ITEM_ID_PROPERTY_KEY, itemId);

    datastoreService.put(favoriteItemEntity);

    favoriteItemDatastore.removeFavoriteItem(USER_EMAIL, itemId);

    Query query = new Query(FAVORITE_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);
    List<Entity> entityList = queryResults.asList(FetchOptions.Builder.withDefaults());

    Assert.assertTrue(entityList.isEmpty());

    Optional<EntertainmentItem> entertainmentItem = entertainmentItemDatastore.queryItem(itemId);

    Assert.assertTrue(entertainmentItem.isPresent());
    Assert.assertEquals(
        Long.valueOf(NUMBER_OF_LIKES - 1), entertainmentItem.get().getNumberOfLikes());
  }

  @Test
  public void queryFavoriteIdsWithNonExistentEmail_listIsEmpty() {
    Assert.assertTrue(favoriteItemDatastore.queryFavoriteIds(USER_EMAIL).isEmpty());
  }

  @Test
  public void queryFavoriteIdsWithEmailThatHasLikes_listContainsLikedIds() {
    List<Long> favoriteIds = new ArrayList<>();

    for (Long Id = ITEM_ID; Id < 10; Id++) {
      favoriteItemDatastore.addFavoriteItem(USER_EMAIL, Id);
      favoriteIds.add(Id);
    }

    List<Long> actualIds = favoriteItemDatastore.queryFavoriteIds(USER_EMAIL);

    Assert.assertEquals(favoriteIds.size(), actualIds.size());
    Assert.assertTrue(actualIds.containsAll(favoriteIds));
  }

  @Test
  public void queryEmailsWithNonExistemItemId_listIsEmpty() {
    Assert.assertTrue(favoriteItemDatastore.queryEmails(ITEM_ID).isEmpty());
  }

  @Test
  public void queryEmailsWithItemWithLikes_listContainsEmailsThatLiked() {
    List<String> emails = new ArrayList<>();

    String email = "a";

    for (int i = 0; i < 5; ++i) {
      favoriteItemDatastore.addFavoriteItem(email, ITEM_ID);
      emails.add(email);
      email += "a";
    }

    List<String> actualEmails = favoriteItemDatastore.queryEmails(ITEM_ID);

    Assert.assertEquals(emails.size(), actualEmails.size());
    Assert.assertTrue(actualEmails.containsAll(emails));
  }
}
