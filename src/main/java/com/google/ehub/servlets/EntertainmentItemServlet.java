package com.google.ehub.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.common.collect.Sets;
import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.utility.BlobstoreURLUtility;
import com.google.ehub.utility.NextIncreasingLexicographicStringUtility;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Handles GET and POST requests to manage EntertainmentItem entities
 * stored in Datastore.
 */
@WebServlet("/entertainment-item-data")
public class EntertainmentItemServlet extends HttpServlet {
  private static final String ENTERTAINMENT_ITEM_KIND = "entertainmentItem";
  private static final String SORTING_MODE_KEY = "sortingMode";
  private static final String SEARCH_VALUE_KEY = "searchValue";
  private static final String TITLE_PROPERTY_KEY = "title";
  private static final String LOWERCASE_TITLE_PROPERTY_KEY = "lowercaseTitle";
  private static final String DESCRIPTION_PROPERTY_KEY = "description";
  private static final String IMAGE_URL_PROPERTY_KEY = "imageURL";
  private static final String TITLE_INCREASING_ALPHA_KEY = "TITLE_INCREASING_ALPHABETICAL_ORDER";
  private static final String TITLE_DECREASING_ALPHA_KEY = "TITLE_DECREASING_ALPHABETICAL_ORDER";

  private static final Set<String> SORTING_MODES =
      Sets.newHashSet(TITLE_INCREASING_ALPHA_KEY, TITLE_DECREASING_ALPHA_KEY);

  private static final int MAX_TITLE_CHARS = 64;
  private static final int MAX_DESCRIPTION_CHARS = 2048;

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String searchValue = request.getParameter(SEARCH_VALUE_KEY);
    String sortingMode = request.getParameter(SORTING_MODE_KEY);

    if (!areGETRequestParametersValid(searchValue, sortingMode)) {
      return;
    }

    Query query = createEntertainmentItemQuery(searchValue.trim(), sortingMode);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery queryResults = datastore.prepare(query);

    List<EntertainmentItem> entertainmentItemList = new ArrayList<>();

    for (Entity entertainmentItemEntity : queryResults.asIterable()) {
      String title = (String) entertainmentItemEntity.getProperty(TITLE_PROPERTY_KEY);
      String description = (String) entertainmentItemEntity.getProperty(DESCRIPTION_PROPERTY_KEY);
      String imageURL = (String) entertainmentItemEntity.getProperty(IMAGE_URL_PROPERTY_KEY);

      entertainmentItemList.add(new EntertainmentItem(title, description, imageURL));
    }

    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(entertainmentItemList));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String title = request.getParameter(TITLE_PROPERTY_KEY);
    String description = request.getParameter(DESCRIPTION_PROPERTY_KEY);
    Optional<String> imageURLOptional =
        BlobstoreURLUtility.getUploadURL(request, IMAGE_URL_PROPERTY_KEY);

    if (!arePOSTRequestParametersValid(title, description, imageURLOptional)) {
      return;
    }

    Entity entertainmentItemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);
    entertainmentItemEntity.setProperty(TITLE_PROPERTY_KEY, title);
    entertainmentItemEntity.setProperty(LOWERCASE_TITLE_PROPERTY_KEY, title.toLowerCase());
    entertainmentItemEntity.setProperty(DESCRIPTION_PROPERTY_KEY, description);
    entertainmentItemEntity.setProperty(IMAGE_URL_PROPERTY_KEY, imageURLOptional.get());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(entertainmentItemEntity);

    response.sendRedirect("/index.html");
  }

  /**
   * Creates a query to search for a list of entertainment items with appropriate
   * search filters and sorting order.
   *
   * @param searchValue the search value used by the query
   * @param sortingMode the sorting mode used by the query
   * @return query built from the respective filter and sorting parameters
   */
  Query createEntertainmentItemQuery(String searchValue, String sortingMode) {
    Query query = new Query(ENTERTAINMENT_ITEM_KIND);

    if (!searchValue.isEmpty()) {
      FilterPredicate greaterThanOrEqualFilter = new FilterPredicate(LOWERCASE_TITLE_PROPERTY_KEY,
          FilterOperator.GREATER_THAN_OR_EQUAL, searchValue.toLowerCase());

      FilterPredicate lessThanFilter =
          new FilterPredicate(LOWERCASE_TITLE_PROPERTY_KEY, FilterOperator.LESS_THAN,
              NextIncreasingLexicographicStringUtility.getNextIncreasingLexicographicString(
                  searchValue.toLowerCase()));

      query =
          query.setFilter(CompositeFilterOperator.and(greaterThanOrEqualFilter, lessThanFilter));
    }

    if (sortingMode.equals(TITLE_INCREASING_ALPHA_KEY)) {
      query = query.addSort(LOWERCASE_TITLE_PROPERTY_KEY, SortDirection.ASCENDING);
    } else if (sortingMode.equals(TITLE_DECREASING_ALPHA_KEY)) {
      query = query.addSort(LOWERCASE_TITLE_PROPERTY_KEY, SortDirection.DESCENDING);
    }

    return query;
  }

  /**
   * Verifies if the parameters given in the HTTP GET Request are valid.
   *
   * @param searchValue the search value given in the GET request parameter
   * @param sortingMode the sorting mode given in the GET request parameter
   * @return true if the parameters given in the GET request are valid, false otherwise
   */
  private static boolean areGETRequestParametersValid(String searchValue, String sortingMode) {
    if (searchValue == null) {
      return false;
    }

    if (sortingMode == null || !SORTING_MODES.contains(sortingMode)) {
      return false;
    }

    return true;
  }

  /**
   * Verifies if the parameters given in the HTTP POST Request are valid.
   *
   * @param title the title given in the POST request parameter
   * @param description the description given in the POST request parameter
   * @param imageURLOptional the image URL given by Blobstore wrapped in an {@link Optional}
   * @return true if the parameters given in the POST request are valid, false otherwise
   */
  private static boolean arePOSTRequestParametersValid(
      String title, String description, Optional<String> imageURLOptional) {
    if (title == null || title.isEmpty() || title.length() > MAX_TITLE_CHARS) {
      return false;
    }

    if (description == null || description.isEmpty()
        || description.length() > MAX_DESCRIPTION_CHARS) {
      return false;
    }

    if (imageURLOptional == null || !imageURLOptional.isPresent()) {
      return false;
    }

    return true;
  }
}
