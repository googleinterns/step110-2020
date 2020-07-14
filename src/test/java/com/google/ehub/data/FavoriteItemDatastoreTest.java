package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.List;

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
  public void addFavoriteItemToDatastore_EntityGetsAddedWithValidKindAndProperties() {
    favoriteItemDatastore.addFavoriteItem(USER_EMAIL, ITEM_ID);

    Query query = new Query(FAVORITE_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);
    List<Entity> entityList = queryResults.asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(1, entityList.size());
    Assert.assertEquals(USER_EMAIL, entityList.get(0).getProperty(USER_EMAIL_PROPERTY_KEY));
    Assert.assertEquals(ITEM_ID, entityList.get(0).getProperty(ITEM_ID_PROPERTY_KEY));
  }

  @Test
  public void queryFavoriteIdsWithNonExistentEmailParam_ListIsEmpty() {
    List<Long> favoriteItems = favoriteItemDatastore.queryFavoriteIds(USER_EMAIL);

    Assert.assertTrue(favoriteItems.isEmpty());
  }
}
