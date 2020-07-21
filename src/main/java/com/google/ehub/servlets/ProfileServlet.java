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
import com.google.ehub.data.UserData;
import com.google.ehub.data.UserProfile;
import com.google.ehub.utility.UserRecommendationUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
  private static final String NEEDS_PROFILE = "NeedsProfile";

  private final UserService userService = UserServiceFactory.getUserService();
  private final ProfileDatastore profileData = new ProfileDatastore();
  private final UserRecommendationUtils recommendationUtils = new UserRecommendationUtils();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String email = userService.getCurrentUser().getEmail();
    String name = request.getParameter(NAME_PROPERTY_KEY);
    String username = request.getParameter(USERNAME_PROPERTY_KEY);
    String bio = request.getParameter(BIO_PROPERTY_KEY);
    boolean edit = Boolean.parseBoolean(request.getParameter(EDIT_PROPERTY_KEY));

    if (!areValidParameters(name, username, bio)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post Request parameters empty");
      return;
    }
    if (edit) {
      profileData.editProfile(name, username, bio);
    } else {
      profileData.addUserProfileToDatastore(name, email, username, bio);
    }

    response.sendRedirect("/ProfilePage.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User must logged in");
    } else {
      String userEmail = userService.getCurrentUser().getEmail();
      UserProfile userProfile = profileData.getUserProfile(userEmail);

      if (userProfile == null) {
        JsonObject profileJson = new JsonObject();
        profileJson.addProperty(NEEDS_PROFILE, true);
        response.setContentType("application/json");
        response.getWriter().println(profileJson);
      } else {
        response.setContentType("application/json");
        response.getWriter().println(new Gson().toJson(
            new UserData(userProfile, recommendationUtils.getRecommendedEmails(userEmail))));
      }
    }
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
  private boolean areValidParameters(String name, String username, String bio) {
    return (name != null && !name.isEmpty() && username != null && !username.isEmpty()
        && bio != null && !bio.isEmpty());
  }
}
