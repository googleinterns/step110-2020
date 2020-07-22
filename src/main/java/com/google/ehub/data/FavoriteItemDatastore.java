package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.Set;
import java.util.HashSet;

/**
 * Singleton class that manages the addition and querying of FavoriteItems stored in Datastore.
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
   * Adds a favorite item Entity to Datastore if it doesn't exist yet.
   *
   * @param userEmail The email of the user that liked the item
   * @param itemId the id of the item that was liked
   */
  public void addFavoriteItem(String userEmail, Long itemId) {
    if (doesFavoriteItemExist(userEmail, itemId)) {
      return;
    }

    Entity favoriteItemEntity = new Entity(FAVORITE_ITEM_KIND);
    favoriteItemEntity.setProperty(USER_EMAIL_PROPERTY_KEY, userEmail);
    favoriteItemEntity.setProperty(ITEM_ID_PROPERTY_KEY, itemId);

    datastoreService.put(favoriteItemEntity);
  }

  /**
   * Removes a favorite item Entity from Datastore if it exists.
   *
   * @param userEmail The email of the user that liked the item
   * @param itemId the id of the item that was liked
   */
  public void removeFavoriteItem(String userEmail, Long itemId) {
    Entity favoriteItemEntity = getFavoriteItemEntity(userEmail, itemId);

    if (favoriteItemEntity == null) {
      return;
    }

    datastoreService.delete(favoriteItemEntity.getKey());
  }

  /**
   * Queries the Ids that were liked by a given user email.
   *
   * @param userEmail the email used to search for the liked items
   * @return set holding the item Ids liked by the user with the given email
   */
  public Set<Long> queryFavoriteIds(String userEmail) {
    Query query = new Query(FAVORITE_ITEM_KIND)
                      .setFilter(new FilterPredicate(
                          USER_EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, userEmail));
    PreparedQuery queryResults = datastoreService.prepare(query);

    Set<Long> favoriteIds = new HashSet<>();

    for (Entity itemEntity : queryResults.asIterable()) {
      favoriteIds.add((Long) itemEntity.getProperty(ITEM_ID_PROPERTY_KEY));
    }

    return favoriteIds;
  }

  /**
   * Queries the emails that have liked a given item.
   *
   * @param itemId the Id of the item that the users to search for have liked
   * @return set holding the emails have have liked the given item Id
   */
  public Set<String> queryEmails(Long itemId) {
    Query query =
        new Query(FAVORITE_ITEM_KIND)
            .setFilter(new FilterPredicate(ITEM_ID_PROPERTY_KEY, FilterOperator.EQUAL, itemId));
    PreparedQuery queryResults = datastoreService.prepare(query);

    Set<String> emails = new HashSet<>();

    for (Entity itemEntity : queryResults.asIterable()) {
      emails.add((String) itemEntity.getProperty(USER_EMAIL_PROPERTY_KEY));
    }

    return emails;
  }

  /**
   * Uses a query to check if a favorite item relation already exists in Datastore.
   *
   * @param userEmail the email that liked the item
   * @param itemId the item that was liked by the user
   * @return true if the favorite item exists in Datastore, otherwise false
   */
  private boolean doesFavoriteItemExist(String userEmail, Long itemId) {
    return getFavoriteItemEntity(userEmail, itemId) != null;
  }

  private Entity getFavoriteItemEntity(String userEmail, Long itemId) {
    Query query =
        new Query(FAVORITE_ITEM_KIND)
            .setFilter(CompositeFilterOperator.and(
                new FilterPredicate(USER_EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, userEmail),
                new FilterPredicate(ITEM_ID_PROPERTY_KEY, FilterOperator.EQUAL, itemId)));

    return datastoreService.prepare(query).asSingleEntity();
  }
}
