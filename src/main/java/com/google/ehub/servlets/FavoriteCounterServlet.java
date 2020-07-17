package com.google.ehub.servlets;

import com.google.ehub.data.FavoriteItemDatastore;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Handles Get requests to get the users that liked a specific entertainment item.
 */
@WebServlet("/favorite-counter")
public class FavoriteCounterServlet extends HttpServlet {
  private static final String ITEM_ID_PARAMETER_KEY = "itemId";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String itemId = request.getParameter(ITEM_ID_PARAMETER_KEY);

    if (!isGetRequestParameterValid(itemId)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "FavoriteCounterServlet: Get request parameter was not specified correctly!");
      return;
    }

    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(
        FavoriteItemDatastore.getInstance().queryEmails(Long.parseLong(itemId)).size()));
  }

  private static boolean isGetRequestParameterValid(String itemId) {
    return itemId != null && !itemId.isEmpty() && NumberUtils.isParsable(itemId);
  }
}
