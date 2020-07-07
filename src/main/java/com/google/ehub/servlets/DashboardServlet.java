package com.google.ehub.servlets;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.data.EntertainmentItemDatastore;
import com.google.ehub.data.EntertainmentItemList;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.EnumUtils;

/*
 * Handles GET requests to retrieve EntertainmentItem entities
 * stored in Datastore and make them available to the Dashboard.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
  private static final String CURSOR_PARAMETER_KEY = "cursor";
  private static final String SEARCH_VALUE_PARAMETER_KEY = "searchValue";
  private static final String SORTING_DIRECTION_PARAMETER_KEY = "sortingDirection";

  private static final int PAGE_SIZE = 18;
  private static final int MAX_SEARCH_VALUE_CHARS = 150;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String searchValue = request.getParameter(SEARCH_VALUE_PARAMETER_KEY);
    String sortingDirection = request.getParameter(SORTING_DIRECTION_PARAMETER_KEY);

    if (!areGetRequestParametersValid(searchValue, sortingDirection)) {
      System.err.println("DashboardServlet: Get Request parameters not specified!");
      return;
    }

    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(PAGE_SIZE);
    String cursorValue = request.getParameter(CURSOR_PARAMETER_KEY);

    if (cursorValue != null) {
      try {
        fetchOptions.startCursor(Cursor.fromWebSafeString(cursorValue));
      } catch (IllegalArgumentException e) {
        System.err.println("DashboardServlet: Cursor value is invalid!");
      }
    }

    SortDirection sortDir = SortDirection.valueOf(sortingDirection);
    EntertainmentItemList itemList;

    if (searchValue.isEmpty()) {
      itemList = EntertainmentItemDatastore.getInstance().queryAllItemsWithTitleOrder(
          fetchOptions, sortDir);
    } else {
      itemList = EntertainmentItemDatastore.getInstance().queryItemsByTitlePrefix(
          fetchOptions, searchValue, sortDir);
    }
    
    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(itemList));
  }

  /**
   * Verifies if the parameters given in the HTTP Get Request are valid.
   *
   * @param searchValue the search value given in the Get request parameter
   * @param sortingDirection the sorting direction given in the Get request parameter
   * @return true if the parameters given in the Get request are valid, false otherwise
   */
  private static boolean areGetRequestParametersValid(String searchValue, String sortingDirection) {
    return (searchValue != null && searchValue.length() <= MAX_SEARCH_VALUE_CHARS)
        && sortingDirection != null && EnumUtils.isValidEnum(SortDirection.class, sortingDirection);
  }
}
