package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FavoriteItemDatastoreTest {
  private static final String FAVORITE_ITEM_KIND = "favoriteItem";
  private static final String USER_EMAIL_PROPERTY_KEY = "userEmail";
  private static final String ITEM_ID_PROPERTY_KEY = "itemId";

  private static final String USER_EMAIL = "bryan@gmail.com";
  private static final Long ITEM_ID = 1L;

  private final FavoriteItemDatastore favoriteItemDatastore = FavoriteItemDatastore.getInstance();
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
    favoriteItemDatastore.addFavoriteItem(USER_EMAIL, ITEM_ID);

    Query query = new Query(FAVORITE_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);
    List<Entity> entityList = queryResults.asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(1, entityList.size());
    Assert.assertEquals(USER_EMAIL, entityList.get(0).getProperty(USER_EMAIL_PROPERTY_KEY));
    Assert.assertEquals(ITEM_ID, entityList.get(0).getProperty(ITEM_ID_PROPERTY_KEY));
  }

  @Test
  public void addFavoriteItemToDatastoreWithDuplicateItems_onlyOneEntityGetsAdded() {
    Entity favoriteItemEntity = new Entity(FAVORITE_ITEM_KIND);
    favoriteItemEntity.setProperty(USER_EMAIL_PROPERTY_KEY, USER_EMAIL);
    favoriteItemEntity.setProperty(ITEM_ID_PROPERTY_KEY, ITEM_ID);

    datastoreService.put(favoriteItemEntity);

    favoriteItemDatastore.addFavoriteItem(USER_EMAIL, ITEM_ID);

    Query query = new Query(FAVORITE_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);
    List<Entity> entityList = queryResults.asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(1, entityList.size());
  }

  @Test
  public void removeFavoriteItemFromDatastore_entityGetsDeleted() {
    Entity favoriteItemEntity = new Entity(FAVORITE_ITEM_KIND);
    favoriteItemEntity.setProperty(USER_EMAIL_PROPERTY_KEY, USER_EMAIL);
    favoriteItemEntity.setProperty(ITEM_ID_PROPERTY_KEY, ITEM_ID);

    datastoreService.put(favoriteItemEntity);

    favoriteItemDatastore.removeFavoriteItem(USER_EMAIL, ITEM_ID);

    Query query = new Query(FAVORITE_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);
    List<Entity> entityList = queryResults.asList(FetchOptions.Builder.withDefaults());

    Assert.assertTrue(entityList.isEmpty());
  }

  @Test
  public void queryFavoriteIdsWithNonExistentEmail_setIsEmpty() {
    Assert.assertTrue(favoriteItemDatastore.queryFavoriteIds(USER_EMAIL).isEmpty());
  }

  @Test
  public void queryFavoriteIdsWithEmailThatHasLikes_setContainsLikedIds() {
    Set<Long> favoriteIds = new HashSet<>();

    for (Long Id = 0L; Id < 5; Id++) {
      favoriteItemDatastore.addFavoriteItem(USER_EMAIL, Id);
      favoriteIds.add(Id);
    }

    Set<Long> actualIds = favoriteItemDatastore.queryFavoriteIds(USER_EMAIL);

    Assert.assertTrue(actualIds.equals(favoriteIds));
  }

  @Test
  public void queryEmailsWithNonExistemItemId_setIsEmpty() {
    Assert.assertTrue(favoriteItemDatastore.queryEmails(ITEM_ID).isEmpty());
  }

  @Test
  public void queryEmailsWithItemWithLikes_setContainsEmailsThatLiked() {
    Set<String> emails = new HashSet<>();

    String email = "a";

    for (int i = 0; i < 5; ++i) {
      favoriteItemDatastore.addFavoriteItem(email, ITEM_ID);
      emails.add(email);
      email += "a";
    }

    Set<String> actualEmails = favoriteItemDatastore.queryEmails(ITEM_ID);

    Assert.assertTrue(actualEmails.equals(emails));
  }
}
