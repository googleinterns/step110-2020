package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;

/**
* Finds a single entity using the email passed in and returns a UserProfile object from the found entity.  
* 
* @param email the email of the User being added to Datastore
* @return a UserProfile object using the entity found
*/
public class UserDataManager {
  public UserProfile getUserProfile(String email) {
    if(email == null){
      return null;
    }
    ProfileDatastore profileDatastore = new ProfileDatastore();
    Filter propertyFilter =
        new FilterPredicate(ProfileDatastore.EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, email);
    Query query = new Query(ProfileDatastore.PROFILE_ITEM_KIND).setFilter(propertyFilter);

    PreparedQuery queryResults = DatastoreServiceFactory.getDatastoreService().prepare(query);
    Entity userEntity = queryResults.asSingleEntity();

    return profileDatastore.createUserProfileFromEntity(userEntity);
  }
}
