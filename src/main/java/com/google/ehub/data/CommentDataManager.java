package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.*;

public class CommentDataManager{

<<<<<<< HEAD
  public void addItemComment(long itemId, String message, long timestamp){
    Entity commentEntity = new Entity("ItemPageComments");
    commentEntity.setProperty("itemId", itemId);
=======
  public void addItemComment(String message, long timestamp){
    Entity commentEntity = new Entity("ItemPageComments");
>>>>>>> 3ccab2bc0d5465bb8e2ae6e00009a398744dc0b3
    commentEntity.setProperty("message", message);
    commentEntity.setProperty("timestamp",timestamp);

    DatastoreService commentDatastoreInitialization = DatastoreServiceFactory.getDatastoreService();
    commentDatastoreInitialization.put(commentEntity);

  }

<<<<<<< HEAD
  public ArrayList<CommentData> retrieveItemComment(long itemId){
=======
  public ArrayList<CommentData> retrieveItemComment(){
>>>>>>> 3ccab2bc0d5465bb8e2ae6e00009a398744dc0b3
    Query itemCommentQuery = new Query("ItemPageComments").addSort("timestamp",SortDirection.ASCENDING);
    DatastoreService itemCommentDatastoreRetrieval = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery itemCommentResults = itemCommentDatastoreRetrieval.prepare(itemCommentQuery);
    ArrayList<CommentData> results = new ArrayList<>();
    for (Entity entity : itemCommentResults.asIterable()) {
<<<<<<< HEAD
      if(entity.getProperty("itemId") == itemId){
=======
>>>>>>> 3ccab2bc0d5465bb8e2ae6e00009a398744dc0b3
      String message = (String) entity.getProperty("message");
      long timestamp = (Long) entity.getProperty("timestamp");
      CommentData comments = new CommentData(message,timestamp);
      results.add(comments);
<<<<<<< HEAD
    }
=======
>>>>>>> 3ccab2bc0d5465bb8e2ae6e00009a398744dc0b3
  }
    return results;
    
 }

}
