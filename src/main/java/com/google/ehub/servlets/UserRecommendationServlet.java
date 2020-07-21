package com.google.ehub.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.ehub.utility.UserRecommendationUtils;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles Get requests to get the recommended users for the user that is logged in.
 */
@WebServlet("/user-recommendation")
public class UserRecommendationServlet extends HttpServlet {
  private static final int MAX_NUMBER_OF_RECOMMENDATIONS = 10;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "UserRecommendationServlet: User is not logged in!");
      return;
    }

    response.setContentType("application/json");
    response.getWriter().println(
        new Gson().toJson(UserRecommendationUtils.getRecommendedEmails(
            userService.getCurrentUser().getEmail(), MAX_NUMBER_OF_RECOMMENDATIONS)));
  }
}
