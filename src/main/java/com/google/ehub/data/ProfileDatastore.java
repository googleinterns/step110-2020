package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.servlets.LoginServlet;
import java.io.IOException;


/**Manages the User Profiles stored in Datastore.**/
public final class ProfileDatastore {

  public static final String PROFILE_ITEM_KIND = "profile";
  public static final String NAME_PROPERTY_KEY = "name";
  public static final String EMAIL_PROPERTY_KEY = "email";
  public static final String USERNAME_PROPERTY_KEY = "username";
  public static final String BIO_PROPERTY_KEY = "bio";

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  public  void addUserProfileToDatastore(String name, String email, String username, String bio) {
    Entity userEntity = new Entity(PROFILE_ITEM_KIND);
    LoginServlet login_instance = new LoginServlet();
    
    userEntity.setProperty(NAME_PROPERTY_KEY, name);
    userEntity.setProperty(EMAIL_PROPERTY_KEY, login_instance.getEmail());
    userEntity.setProperty(USERNAME_PROPERTY_KEY, username);
    userEntity.setProperty(BIO_PROPERTY_KEY, bio);

    datastore.put(userEntity);
  }

  public  UserProfile createUserProfileFromEntity(Entity userEntity) {
    long id = userEntity.getKey().getId();
    String name = (String) userEntity.getProperty(NAME_PROPERTY_KEY);
    String username = (String) userEntity.getProperty(USERNAME_PROPERTY_KEY);
    String bio = (String) userEntity.getProperty(BIO_PROPERTY_KEY);
    String email = (String) userEntity.getProperty(EMAIL_PROPERTY_KEY);

    return new UserProfile(name, username, bio, email);
  }
  public String getEmailProperty(){
    return EMAIL_PROPERTY_KEY;
  }
  public String getItemKind(){
    return PROFILE_ITEM_KIND;
  }
  public  DatastoreService getDatastore(){
      return datastore;
  }
    
}
