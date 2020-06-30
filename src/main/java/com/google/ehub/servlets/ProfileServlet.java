package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.ehub.data.ProfileDatastore;
import com.google.ehub.data.UserDataManager;
import com.google.ehub.data.UserProfile;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns HTML that contains the user's profile information.**/
@WebServlet("/profile-data")
public class ProfileServlet extends HttpServlet {
  
  private static final String NAME_PROPERTY_KEY = "name";
  private static final String EMAIL_PROPERTY_KEY = "email";
  private static final String USERNAME_PROPERTY_KEY = "username";
  private static final String BIO_PROPERTY_KEY = "bio";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ProfileDatastore profileData = new ProfileDatastore();

    String name = request.getParameter(NAME_PROPERTY_KEY);
    String email = request.getParameter(EMAIL_PROPERTY_KEY);
    String username = request.getParameter(USERNAME_PROPERTY_KEY );
    String bio = request.getParameter(BIO_PROPERTY_KEY);

    if (!areValidParameters(name, email, username, bio)) {
      System.err.println("ProfileServlet: Post Request parameters empty");
      return;
    }

    profileData.addUserProfileToDatastore(name, email, username, bio);
    response.sendRedirect("/ProfilePage.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserDataManager manager = new UserDataManager();
    LoginServlet login = new LoginServlet();
    String useremail = login.getEmail();
    UserProfile newUser = manager.getUserProfile(useremail);

    response.setContentType("application/json");
    response.getWriter().println(convertToJson(newUser));
  }

  private String convertToJson(UserProfile profile) {
    return new Gson().toJson(profile);
  }

  private boolean areValidParameters(String name, String email, String username, String bio) {
    return (name != null && !name.isEmpty() && email != null && !email.isEmpty() && username != null
        && !username.isEmpty() && bio != null && !bio.isEmpty());
  }
}