package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public final class FavoriteItemDatastore {
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
}
