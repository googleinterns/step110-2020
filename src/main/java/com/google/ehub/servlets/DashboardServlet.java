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

/*
 * Handles GET requests to retrieve EntertainmentItem entities
 * stored in Datastore and make them available to the Dashboard.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
  private static final String CURSOR_PARAMETER_KEY = "cursor";
  private static final String SEARCH_VALUE_PARAMETER_KEY = "searchValue";
  private static final String SORT_TYPE_PARAMETER_KEY = "sortType";
  private static final String ASCENDING_TITLE_SORT = "ASCENDING_TITLE";
  private static final String DESCENDING_TITLE_SORT = "DESCENDING_TITLE";
  private static final String RECENT_RELEASE_DATE_SORT = "RECENT_RELEASE_DATE";

  private static final int PAGE_SIZE = 18;
  private static final int MAX_SEARCH_VALUE_CHARS = 150;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String searchValue = request.getParameter(SEARCH_VALUE_PARAMETER_KEY);
    String sortType = request.getParameter(SORT_TYPE_PARAMETER_KEY);

    if (!areGetRequestParametersValid(searchValue, sortType)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "DashboardServlet: Get Request parameters not specified correctly!");
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

    response.setContentType("application/json");
    response.getWriter().println(
        new Gson().toJson(getItemList(fetchOptions, searchValue, sortType)));
  }

  /**
   * Verifies if the parameters given in the HTTP Get Request are valid.
   *
   * @param searchValue the search value given in the Get request parameter
   * @param sortType the sort type given in the Get request parameter
   * @return true if the parameters given in the Get request are valid, false
   *     otherwise
   */
  private static boolean areGetRequestParametersValid(String searchValue, String sortType) {
    return (searchValue != null && searchValue.length() <= MAX_SEARCH_VALUE_CHARS)
        && (sortType != null
            && (sortType.equals(ASCENDING_TITLE_SORT) || sortType.equals(DESCENDING_TITLE_SORT)
                || sortType.equals(RECENT_RELEASE_DATE_SORT)));
  }

  private static EntertainmentItemList getItemList(
      FetchOptions fetchOptions, String searchValue, String sortType) {
    EntertainmentItemDatastore itemDatastore = EntertainmentItemDatastore.getInstance();

    if (sortType.equals(ASCENDING_TITLE_SORT)) {
      return itemDatastore.queryItemsByTitlePrefix(
          fetchOptions, searchValue, SortDirection.ASCENDING);
    } else if (sortType.equals(DESCENDING_TITLE_SORT)) {
      return itemDatastore.queryItemsByTitlePrefix(
          fetchOptions, searchValue, SortDirection.DESCENDING);
    } else {
      return itemDatastore.queryItemsByReleaseDate(fetchOptions, SortDirection.DESCENDING);
    }
  }
}
