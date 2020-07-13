package com.google.ehub.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/favorite-item")
public class FavoriteItemServlet extends HttpServlet {
  private static final String FAVORITE_ITEM_ID_PARAMETER_KEY = "favoriteItemId";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    response.setContentType("application/json");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String favoriteItemId =
        request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY);

    if (!isPostRequestParameterValid(favoriteItemId)) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST,
          "FavoriteItemServlet: Post request parameter not specified correctly!");
      return;
    }

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                         "FavoriteItemServlet: User is not logged in!");
      return;
    }

    
  }

  private static boolean isPostRequestParameterValid(String favoriteItemId) {
    return favoriteItemId != null && !favoriteItemId.isEmpty();
  }
}
