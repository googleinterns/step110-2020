package com.google.ehub.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.ehub.data.EntertainmentItemDatastore;
import com.google.ehub.data.FavoriteItemDatastore;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Handles Get and Post requests for items favorited by the user that is logged in.
 */
@WebServlet("/favorite-item")
public class FavoriteItemServlet extends HttpServlet {
  private static final String FAVORITE_ITEM_ID_PARAMETER_KEY = "favoriteItemId";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "FavoriteItemServlet: User is not logged in!");
      return;
    }

    response.setContentType("application/json");
    response.getWriter().println(
        new Gson().toJson(FavoriteItemDatastore.getInstance().queryFavoriteIds(
            userService.getCurrentUser().getEmail())));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String favoriteItemIdParameter = request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY);

    if (!isPostRequestParameterValid(favoriteItemIdParameter)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "FavoriteItemServlet: Post request parameter not specified correctly!");
      return;
    }

    Long itemId = Long.parseLong(favoriteItemIdParameter);

    if (!doesItemExist(itemId)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "FavoriteItemServlet: Favorite Item Id doesn't map to a known item!");
      return;
    }

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "FavoriteItemServlet: User is not logged in!");
      return;
    }

    FavoriteItemDatastore.getInstance().addFavoriteItem(
        userService.getCurrentUser().getEmail(), itemId);
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String favoriteItemIdParameter = request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY);

    if (!isPostRequestParameterValid(favoriteItemIdParameter)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "FavoriteItemServlet: Post request parameter not specified correctly!");
      return;
    }

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "FavoriteItemServlet: User is not logged in!");
      return;
    }

    FavoriteItemDatastore.getInstance().removeFavoriteItem(
        userService.getCurrentUser().getEmail(), Long.parseLong(favoriteItemIdParameter));
  }

  private static boolean isPostRequestParameterValid(String favoriteItemIdParameter) {
    return favoriteItemIdParameter != null && !favoriteItemIdParameter.isEmpty()
        && NumberUtils.isParsable(favoriteItemIdParameter);
  }

  private static boolean doesItemExist(Long itemId) {
    return EntertainmentItemDatastore.getInstance().queryItem(itemId).isPresent();
  }
}
