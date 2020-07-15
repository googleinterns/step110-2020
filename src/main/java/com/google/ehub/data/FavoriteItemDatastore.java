package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that manages FavoriteItems stored in Datastore.
 */
public final class FavoriteItemDatastore {
  private static final String FAVORITE_ITEM_KIND = "favoriteItem";
  private static final String USER_EMAIL_PROPERTY_KEY = "userEmail";
  private static final String ITEM_ID_PROPERTY_KEY = "itemId";

  private static FavoriteItemDatastore instance;

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

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
    Query query = new Query(FAVORITE_ITEM_KIND)
                      .setFilter(new FilterPredicate(
                          USER_EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, userEmail));
    PreparedQuery queryResults = datastoreService.prepare(query);

    List<Long> favoriteIds = new ArrayList<>();

    for (Entity itemEntity : queryResults.asIterable()) {
      favoriteIds.add((Long) itemEntity.getProperty(ITEM_ID_PROPERTY_KEY));
    }

    return favoriteIds;
  }

  /**
   * Queries the emails that have liked a given item.
   *
   * @param itemId the Id of the item that the users to search for have liked
   * @return list holding the emails have have liked the given item Id
   */
  public List<String> queryEmails(Long itemId) {
    Query query =
        new Query(FAVORITE_ITEM_KIND)
            .setFilter(new FilterPredicate(ITEM_ID_PROPERTY_KEY, FilterOperator.EQUAL, itemId));
    PreparedQuery queryResults = datastoreService.prepare(query);

    List<String> emails = new ArrayList<>();

    for (Entity itemEntity : queryResults.asIterable()) {
      emails.add((String) itemEntity.getProperty(USER_EMAIL_PROPERTY_KEY));
    }

    return emails;
  }
}
