package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;

public final class FavoriteItemDatastore {
  private static final String FAVORITE_ITEM_KIND = "favoriteItem";
  private static final String USER_EMAIL_PROPERTY_KEY = "userEmail";
  private static final String ITEM_ID_PROPERTY_KEY = "itemId";

  private static FavoriteItemDatastore instance;

  private final DatastoreService datastoreService =
      DatastoreServiceFactory.getDatastoreService();

  private FavoriteItemDatastore() {}

  /**
   * Gives access to the single instance of the class, and creates this instance
   * if it was not initialized previously.
   *
   * @return single instance of the class
   */
  public static FavoriteItemDatastore getInstance() {
    if (instance == null) {
      instance = new FavoriteItemDatastore();
    }

    return instance;
  }

  /**
   * Adds a favorite item Entity to Datastore.
   *
   * @param userEmail The email of the user that liked the item
   * @param itemId the id of the item that was liked
   */
  public void addFavoriteItem(String userEmail, Long itemId) {
    Entity favoriteItemEntity = new Entity(FAVORITE_ITEM_KIND);
    favoriteItemEntity.setProperty(USER_EMAIL_PROPERTY_KEY, userEmail);
    favoriteItemEntity.setProperty(ITEM_ID_PROPERTY_KEY, itemId);

    datastoreService.put(favoriteItemEntity);
  }

  /**
   * Queries the Ids that were liked by a given user email.
   *
   * @param userEmail the email used to search for the liked items
   * @return list holding the item Ids liked by the user with the given email
   */
  public List<Long> queryFavoriteIds(String userEmail) {
    Query query = new Query(FAVORITE_ITEM_KIND);
    PreparedQuery queryResults = datastoreService.prepare(query);

    List<Long> favoriteIds = new ArrayList<>();

    for (Entity itemEntity : queryResults.asIterable()) {
      favoriteIds.add((Long)itemEntity.getProperty(ITEM_ID_PROPERTY_KEY));
    }

    return favoriteIds;
  }
}
