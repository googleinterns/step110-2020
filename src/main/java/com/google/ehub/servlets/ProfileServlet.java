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
import com.google.ehub.data.FavoriteItemDatastore;
import com.google.ehub.data.ProfileDatastore;
import com.google.ehub.data.UserData;
import com.google.ehub.data.UserProfile;
import com.google.ehub.utility.UserRecommendationUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
  private static final String NAME_PARAMETER_KEY = "name";
  private static final String EMAIL_PARAMETER_KEY = "email";
  private static final String USERNAME_PARAMETER_KEY = "username";
  private static final String BIO_PARAMETER_KEY = "bio";
  private static final String EDIT_PARAMETER_KEY = "edit";
  private static final String NEEDS_PROFILE = "NeedsProfile";

  private final UserService userService = UserServiceFactory.getUserService();
  private final ProfileDatastore profileData = new ProfileDatastore();
  private final FavoriteItemDatastore favoriteItemDatastore = FavoriteItemDatastore.getInstance();
  private final UserRecommendationUtils recommendationUtils = new UserRecommendationUtils();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String email = userService.getCurrentUser().getEmail();
    String name = request.getParameter(NAME_PARAMETER_KEY);
    String username = request.getParameter(USERNAME_PARAMETER_KEY);
    String bio = request.getParameter(BIO_PARAMETER_KEY);
    boolean edit = Boolean.parseBoolean(request.getParameter(EDIT_PARAMETER_KEY));

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
    // Email is an optional parameter used to query a profile that is separate from the email that
    // is logged in.
    String email = request.getParameter(EMAIL_PARAMETER_KEY);
    if (email != null) {
      sendUserProfileWithEmail(response, email);
      return;
    }

    // Username is an optional parameter used to query a profile that is separate from the username
    // that is currently active.
    String username = request.getParameter(USERNAME_PARAMETER_KEY);
    if (username != null) {
      sendUserProfileWithUsername(response, username);
      return;
    }

    if (!userService.isUserLoggedIn()) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "User must be logged in to retrieve profile!");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();
    UserProfile userProfile = profileData.getUserProfile(userEmail);

    if (userProfile == null) {
      JsonObject profileJson = new JsonObject();
      profileJson.addProperty(NEEDS_PROFILE, true);
      response.setContentType("application/json");
      response.getWriter().println(profileJson);
    } else {
      Set<Long> itemIdsLikedByUser = favoriteItemDatastore.queryFavoriteIds(userEmail);
      List<String> recommendedEmails = recommendationUtils.getRecommendedEmails(
          getUsersWhoLikeLoggedInUserItems(userEmail, itemIdsLikedByUser));

      response.setContentType("application/json");
      response.getWriter().println(new Gson().toJson(new UserData(userProfile, recommendedEmails)));
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

  private Map<Long, Set<String>> getUsersWhoLikeLoggedInUserItems(
      String userEmail, Set<Long> itemIds) {
    Map<Long, Set<String>> itemLikes = new HashMap<>();

    for (Long itemId : itemIds) {
      Set<String> emails = favoriteItemDatastore.queryEmails(itemId);

      // Remove the logged in user from the list to ignore it for recommendations.
      emails.remove(userEmail);

      itemLikes.put(itemId, emails);
    }

    return itemLikes;
  }

  private void sendUserProfileWithEmail(HttpServletResponse response, String email)
      throws IOException {
    UserProfile userProfile = profileData.getUserProfile(email);

    if (userProfile == null) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Email specified doesn't have a profile!");
      return;
    }

    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(userProfile));
  }

  private void sendUserProfileWithUsername(HttpServletResponse response, String username)
      throws IOException {
    Optional<UserProfile> userProfile = profileData.queryProfileByUsername(username);

    if (!userProfile.isPresent()) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Username specified doesn't have a profile!");
      return;
    }

    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(userProfile.get()));
  }
}
