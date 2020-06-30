package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that manages CommentData that is kept in the Datastore
 */
public class CommentDataManager {
  private static final String COMMENT_KIND_KEY = "comment";
  private static final String ITEMID_PROPERTY_KEY = "itemId";
  private static final String MESSAGE_PROPERTY_KEY = "message";
  private static final String TIMESTAMP_PROPERTY_KEY = "timestampMillis";

  public void addItemComment(long itemId, String message, long timestampMillis) {
    Entity commentEntity = new Entity(COMMENT_KIND_KEY);
    commentEntity.setProperty(ITEMID_PROPERTY_KEY, itemId);
    commentEntity.setProperty(MESSAGE_PROPERTY_KEY, message);
    commentEntity.setProperty(TIMESTAMP_PROPERTY_KEY, timestampMillis);

    DatastoreService commentDatastoreInitialization = DatastoreServiceFactory.getDatastoreService();
    commentDatastoreInitialization.put(commentEntity);
  }

  public List<CommentData> retrieveComments(long itemId) {
    List<CommentData> results = new ArrayList<>();
    Query itemCommentQuery =
        new Query(COMMENT_KIND_KEY)
            .setFilter(new FilterPredicate(ITEMID_PROPERTY_KEY, FilterOperator.EQUAL, itemId))
            .addSort(TIMESTAMP_PROPERTY_KEY, SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery itemCommentResults = datastore.prepare(itemCommentQuery);
    for (Entity entity : itemCommentResults.asIterable()) {
      String message = (String) entity.getProperty(MESSAGE_PROPERTY_KEY);
      long timestampMillis = (Long) entity.getProperty(TIMESTAMP_PROPERTY_KEY);
      results.add(new CommentData(itemId, message, timestampMillis));
    }
    return results;
  }
}
