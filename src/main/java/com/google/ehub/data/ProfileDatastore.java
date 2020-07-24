package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.ehub.servlets.LoginServlet;
import java.io.IOException;
import java.util.Optional;

/** Manages the User Profiles stored in Datastore. **/
public final class ProfileDatastore {
  private static final String PROFILE_ITEM_KIND = "profile";
  private static final String NAME_PROPERTY_KEY = "name";
  private static final String EMAIL_PROPERTY_KEY = "email";
  private static final String USERNAME_PROPERTY_KEY = "username";
  private static final String BIO_PROPERTY_KEY = "bio";

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final UserService userService = UserServiceFactory.getUserService();

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
    if (userEntity == null) {
      return null;
    }
    String name = (String) userEntity.getProperty(NAME_PROPERTY_KEY);
    String username = (String) userEntity.getProperty(USERNAME_PROPERTY_KEY);
    String bio = (String) userEntity.getProperty(BIO_PROPERTY_KEY);
    String email = (String) userEntity.getProperty(EMAIL_PROPERTY_KEY);

    return new UserProfile(name, username, bio, email);
  }

  /**
   * Finds a single entity using the email passed in and returns a UserProfile object from the found
   * entity.
   *
   * @param email the email of the User being added to Datastore
   * @return a UserProfile object using the entity found
   */
  public UserProfile getUserProfile(String email) {
    if (email == null) {
      return null;
    }
    Filter propertyFilter = new FilterPredicate(EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, email);
    Query query = new Query(PROFILE_ITEM_KIND).setFilter(propertyFilter);

    PreparedQuery queryResults = datastore.prepare(query);
    Entity userEntity = queryResults.asSingleEntity();

    return createUserProfileFromEntity(userEntity);
  }

  /**
   * Finds a user profile based on unique username.
   *
   * @param username the value of the username property to search for
   * @return the UserProfile found in Datastore wrapped in an {@link
   *     Optional}, the
   * optional object will be empty if the user Entity was not found
   */
  public Optional<UserProfile> queryProfileByUsername(String username) {
    Query query =
        new Query(PROFILE_ITEM_KIND)
            .setFilter(new FilterPredicate(USERNAME_PROPERTY_KEY, FilterOperator.EQUAL, username));
    PreparedQuery queryResults = datastore.prepare(query);

    Entity userEntity = queryResults.asSingleEntity();

    return Optional.ofNullable(createUserProfileFromEntity(userEntity));
  }

  /**
   * Changes the property of the User entity with the new edited values and updates the datastore.
   *
   * @param name the new name of the user
   * @param username the new username of the user
   * @param bio the new biography of the user
   */
  public void editProfile(String name, String username, String bio) {
    String email = userService.getCurrentUser().getEmail();

    Filter propertyFilter = new FilterPredicate(EMAIL_PROPERTY_KEY, FilterOperator.EQUAL, email);
    Query query = new Query(PROFILE_ITEM_KIND).setFilter(propertyFilter);

    PreparedQuery queryResults = datastore.prepare(query);
    Entity userEntity = queryResults.asSingleEntity();

    userEntity.setProperty(NAME_PROPERTY_KEY, name);
    userEntity.setProperty(USERNAME_PROPERTY_KEY, username);
    userEntity.setProperty(BIO_PROPERTY_KEY, bio);
    datastore.put(userEntity);
  }
}
