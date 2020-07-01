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

/** Manages the User Profiles stored in Datastore. **/
public final class ProfileDatastore {
  public static final String PROFILE_ITEM_KIND = "profile";
  public static final String NAME_PROPERTY_KEY = "name";
  public static final String EMAIL_PROPERTY_KEY = "email";
  public static final String USERNAME_PROPERTY_KEY = "username";
  public static final String BIO_PROPERTY_KEY = "bio";

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

 /**
   * Adds a the User Profile variables to Datastore.
   *
   * @param name the name of the User being added to Datastore
   * @param email the email of the User being added to Datastore
   * @param username the username of the User being added to Datastore
   * @param bio the bio of the User being added to Datastore
   */
  public void addUserProfileToDatastore(String name, String email, String username, String bio) {
    Entity userEntity = new Entity(PROFILE_ITEM_KIND);

    userEntity.setProperty(NAME_PROPERTY_KEY, name);
    userEntity.setProperty(EMAIL_PROPERTY_KEY, email);
    userEntity.setProperty(USERNAME_PROPERTY_KEY, username);
    userEntity.setProperty(BIO_PROPERTY_KEY, bio);

    datastore.put(userEntity);
  }

  /**
   * Create a UserProfile object from a given entity.
   *
   * @param userEntity the User Entity in the Datastore
   * @return a UserProfile object with correct credentials 
   */
  public UserProfile createUserProfileFromEntity(Entity userEntity) {
    String name = (String) userEntity.getProperty(NAME_PROPERTY_KEY);
    String username = (String) userEntity.getProperty(USERNAME_PROPERTY_KEY);
    String bio = (String) userEntity.getProperty(BIO_PROPERTY_KEY);
    String email = (String) userEntity.getProperty(EMAIL_PROPERTY_KEY);

    return new UserProfile(name, username, bio, email);
  }

  /**
    * Finds a single entity using the email passed in and returns a UserProfile object from the found entity.  
    * 
    * @param email the email of the User being added to Datastore
    * @return a UserProfile object using the entity found
    */
  public UserProfile getUserProfile(String email) {
    if(email == null){
      return null;
    }
    Filter propertyFilter =
        new FilterPredicate(EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, email);
    Query query = new Query(PROFILE_ITEM_KIND).setFilter(propertyFilter);

    PreparedQuery queryResults = datastore.prepare(query);
    Entity userEntity = queryResults.asSingleEntity();

    return createUserProfileFromEntity(userEntity);
  }

}