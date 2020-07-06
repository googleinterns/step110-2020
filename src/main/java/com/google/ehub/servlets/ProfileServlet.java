package com.google.ehub.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.ehub.data.ProfileDatastore;
import com.google.ehub.data.UserProfile;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles adding the form submission to the datastore in
 * POST and returns json of the user's profile information
 * to client.
 */
@WebServlet("/profile-data")
public class ProfileServlet extends HttpServlet {
  private static final String NAME_PROPERTY_KEY = "name";
  private static final String EMAIL_PROPERTY_KEY = "email";
  private static final String USERNAME_PROPERTY_KEY = "username";
  private static final String BIO_PROPERTY_KEY = "bio";
  private static final String EDIT_PROPERTY_KEY = "edit";

  ProfileDatastore profileData = new ProfileDatastore();
  String editValue;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter(NAME_PROPERTY_KEY);
    String email = request.getParameter(EMAIL_PROPERTY_KEY);
    String username = request.getParameter(USERNAME_PROPERTY_KEY);
    String bio = request.getParameter(BIO_PROPERTY_KEY);
    editValue = request.getParameter(EDIT_PROPERTY_KEY);

    if (editValue != null && editValue.equals("true")) {
      profileData.editProfile(name, username, bio);
    } else {
      if (!areValidParameters(name, email, username, bio)) {
        System.err.println("ProfileServlet: Post Request parameters empty");
        return;
      }
      profileData.addUserProfileToDatastore(name, email, username, bio);
    }

    response.sendRedirect("/ProfilePage.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LoginServlet login = new LoginServlet();
    String useremail = login.getEmail();
    UserProfile newUser = profileData.getUserProfile(useremail);

    System.out.println("userEmail:" + useremail);
    System.out.println("new User: " + newUser);
    response.setContentType("application/json");
    response.getWriter().println(convertToJson(newUser));
  }

  /**
   * Creates a json from the UserProfile object.
   *
   * @param profile the UserProfile object
   * @return a json file
   */
  private String convertToJson(UserProfile profile) {
    return new Gson().toJson(profile);
  }

  /**
   * Checks if any of the request values are null.
   *
   * @param name the name of the User
   * @param email the email of the User
   * @param username the username of the User
   * @param bio the bio of the User
   * @return true if not null
   */
  private boolean areValidParameters(String name, String email, String username, String bio) {
    return (name != null && !name.isEmpty() && email != null && !email.isEmpty() && username != null
        && !username.isEmpty() && bio != null && !bio.isEmpty());
  }
}
