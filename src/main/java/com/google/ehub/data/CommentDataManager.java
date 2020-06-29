package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.*;

public class CommentDataManager{

  public void addItemComment(long itemId, String message, long timestamp){
    Entity commentEntity = new Entity("ItemPageComments");
    commentEntity.setProperty("itemId", itemId);
    commentEntity.setProperty("message", message);
    commentEntity.setProperty("timestamp",timestamp);

    DatastoreService commentDatastoreInitialization = DatastoreServiceFactory.getDatastoreService();
    commentDatastoreInitialization.put(commentEntity);

  }

  public ArrayList<CommentData> retrieveItemComment(long itemId){
    Query itemCommentQuery = new Query("ItemPageComments").addSort("timestamp",SortDirection.ASCENDING);
    DatastoreService itemCommentDatastoreRetrieval = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery itemCommentResults = itemCommentDatastoreRetrieval.prepare(itemCommentQuery);
    ArrayList<CommentData> results = new ArrayList<>();
    for (Entity entity : itemCommentResults.asIterable()) {
      if(entity.getProperty("itemId") == itemId){
      String message = (String) entity.getProperty("message");
      long timestamp = (Long) entity.getProperty("timestamp");
      CommentData comments = new CommentData(message,timestamp);
      results.add(comments);
    }
  }
    return results;
    
 }

}
