package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.ehub.data.ProfileDatastore;
import com.google.ehub.data.UserProfile;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that manages CommentData that is kept in the Datastore
 */
public class CommentDataManager {
  private static final String COMMENT_KIND_KEY = "comment";
  public static final String ITEM_ID_PROPERTY_KEY = "itemId";
  public static final String COMMENT_PROPERTY_KEY = "comment";
  private static final String TIMESTAMP_PROPERTY_KEY = "timestampMillis";
  private static final String EMAIL_PROPERTY_KEY = "email";
  private static final String COMMENT_ID_PROPERTY_KEY = "commentId";
  private final UserService userService = UserServiceFactory.getUserService();
  private final ProfileDatastore profileData = new ProfileDatastore();

  /**
   * Method that adds comment information into Datastore by ItemId
   *
   * @param itemId The unique ID referencing an Entertainment item. Generated by Datastore
   * @param comment The message/comment that the user inputs
   * @param timestampMillis The exact timestamp in Milliseconds that the comment is posted
   * @param email The user's email
   */
  public void addItemComment(long itemId, String comment, long timestampMillis, String email) {
    Entity commentEntity = new Entity(COMMENT_KIND_KEY);
    Long commentId = commentEntity.getKey().getId();
    commentEntity.setProperty(ITEM_ID_PROPERTY_KEY, itemId);
    commentEntity.setProperty(COMMENT_PROPERTY_KEY, comment);
    commentEntity.setProperty(TIMESTAMP_PROPERTY_KEY, timestampMillis);
    commentEntity.setProperty(EMAIL_PROPERTY_KEY, email);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    
    }

  /**
   * Method that retrieves comment information from Datastore by ItemId
   *
   * @param itemId The unique ID referencing an Entertainment item. Generated by Datastore
   * @return A list of comments.
   */
  public List<CommentData> retrieveComments(long itemId) {
    List<CommentData> results = new ArrayList<>();
    Query itemCommentQuery =
        new Query(COMMENT_KIND_KEY)
            .setFilter(new FilterPredicate(ITEM_ID_PROPERTY_KEY, FilterOperator.EQUAL, itemId))
            .addSort(TIMESTAMP_PROPERTY_KEY, SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery comments = datastore.prepare(itemCommentQuery);
    for (Entity entity : comments.asIterable()) {
      String storedEmail = (String) entity.getProperty(EMAIL_PROPERTY_KEY);
      UserProfile userProfile = profileData.getUserProfile(storedEmail);
      if(userProfile == null){
        continue;
      }
      String comment = (String) entity.getProperty(COMMENT_PROPERTY_KEY);
      long timestampMillis = (Long) entity.getProperty(TIMESTAMP_PROPERTY_KEY);
      
      String username = userProfile.getUsername(); 
      long commentId = entity.getKey().getId();
      String currentEmail = userService.getCurrentUser().getEmail();
      Boolean belongsToUser;
      boolean belongsToUser = storedEmail.equals(currentEmail);
      results.add(new CommentData(itemId, comment, timestampMillis, username, commentId, COMMENT_KIND_KEY, belongsToUser));
    }
   return results;
}

  public void deleteComment (long commentId, String COMMENT_KIND_KEY) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(KeyFactory.createKey(COMMENT_KIND_KEY, commentId));
  }
}
