package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class UserDataManager {
  public static UserProfile getUserProfile(String email) {
    Filter propertyFilter = new FilterPredicate(EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, email);
    Query q = new Query(PROFILE_ITEM_KIND).setFilter(propertyFilter);
    PreparedQuery queryResults = datastore.prepare(q);
    Entity userEntity = queryResults.asSingleEntity();

    ProfileDatastore.createUserProfileFromEntity(userEntity);

    return ProfileDatastore.createUserProfileFromEntity(userEntity);
    ;
  }
  // TODO(oyins):check if it is null with method
}
