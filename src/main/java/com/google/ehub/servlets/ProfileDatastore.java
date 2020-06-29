package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Manages the User Profiles stored in Datastore.**/
public final class ProfileDatastore {
  public static final String PROFILE_ITEM_KIND = "profile";
  public static final String NAME_PROPERTY_KEY = "name";
  public static final String EMAIL_PROPERTY_KEY = "email";
  public static final String USERNAME_PROPERTY_KEY = "username";
  public static final String BIO_PROPERTY_KEY = "bio";

  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  public void addUserProfileToDatastore(String Name, String Email, String Username, String Bio) {
    Entity userEntity = new Entity(PROFILE_ITEM_KIND);

    userEntity.setProperty(NAME_PROPERTY_KEY, Name);
    userEntity.setProperty(EMAIL_PROPERTY_KEY, LoginServlet.getEmail());
    userEntity.setProperty(USERNAME_PROPERTY_KEY, Username);
    userEntity.setProperty(BIO_PROPERTY_KEY, Bio);

    datastore.put(userEntity);
  }

  private static UserProfile createUserProfileFromEntity(Entity userEntity) {
    long id = entity.getKey().getId();
    String name = (String) entity.getProperty(NAME_PROPERTY_KEY);
    String username = (String) entity.getProperty(USERNAME_PROPERTY_KEY);
    String bio = (String) entity.getProperty(BIO_PROPERTY_KEY);
    String email = (String) entity.getProperty(EMAIL_PROPERTY_KEY);

    return new UserProfile(name, username, bio, email);
  }
}
