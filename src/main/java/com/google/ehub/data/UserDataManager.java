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

public class UserDataManager {
  public UserProfile getUserProfile(String email) {
    ProfileDatastore data = new ProfileDatastore();
    Filter propertyFilter =
        new FilterPredicate(data.EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, email);
    Query query = new Query(data.PROFILE_ITEM_KIND).setFilter(propertyFilter);

    PreparedQuery queryResults = DatastoreServiceFactory.getDatastoreService().prepare(query);
    Entity userEntity = queryResults.asSingleEntity();

    return data.createUserProfileFromEntity(userEntity);
  }
  // TODO(oyins):check if it is null with method
}
