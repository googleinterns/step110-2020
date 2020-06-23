package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.ehub.utility.NextIncreasingLexicographicStringUtility;
import java.util.ArrayList;
import java.util.List;


/**
 * Manages EntertainmentItems stored in Datastore.
 */
public final class EntertainmentItemDatastore {
  public static final String ENTERTAINMENT_ITEM_KIND = "entertainmentItem";
  public static final String TITLE_PROPERTY_KEY = "title";
  public static final String LOWERCASE_TITLE_PROPERTY_KEY = "lowercaseTitle";
  public static final String DESCRIPTION_PROPERTY_KEY = "description";
  public static final String IMAGE_URL_PROPERTY_KEY = "imageURL";

  private static DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  /**
   * Adds an EntertainmentItem to Datastore.
   *
   * @param title title of the entertainment item
   * @param description description of the entertainment item
   * @param imageURL URL to the image of the entertainment item stored in Blobstore
   */
  public static void addEntertainmentItemToDatastore(
      String title, String description, String imageURL) {
    Entity entertainmentItemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);

    entertainmentItemEntity.setProperty(TITLE_PROPERTY_KEY, title);
    entertainmentItemEntity.setProperty(LOWERCASE_TITLE_PROPERTY_KEY, title.toLowerCase());
    entertainmentItemEntity.setProperty(DESCRIPTION_PROPERTY_KEY, description);
    entertainmentItemEntity.setProperty(IMAGE_URL_PROPERTY_KEY, imageURL);

    datastoreService.put(entertainmentItemEntity);
  }

  /**
   * Queries all entertainment items found in Datastore.
   *
   * @return list with all entertainment items found in Datastore
   */
  public static List<EntertainmentItem> queryAllEntertainmentItems() {
    return getListFromQuery(new Query(ENTERTAINMENT_ITEM_KIND));
  }

  /**
   * Queries all entertainment items found in Datastore and orders them based on title with the
   * given sorting direction
   */
  public static List<EntertainmentItem> queryAllEntertainmentItemsWithTitleOrder(
      SortDirection sortDirection) {
    return getListFromQuery(
        new Query(ENTERTAINMENT_ITEM_KIND).addSort(LOWERCASE_TITLE_PROPERTY_KEY, sortDirection));
  }

  /**
   * Queries entertainment items with the specified title prefix and sorting direction.
   *
   * @param title the title prefix used to filter the query
   * @param sortDirection the sort direction used to order the entertainment items based on title
   * @return list with the entertainment items that match the title prefix and sorting direction
   */
  public static List<EntertainmentItem> queryEntertainmentItemsByTitle(
      String title, SortDirection sortDirection) {
    return getListFromQuery(
        new Query(ENTERTAINMENT_ITEM_KIND)
            .addSort(LOWERCASE_TITLE_PROPERTY_KEY, sortDirection)
            .setFilter(CompositeFilterOperator.and(
                new FilterPredicate(LOWERCASE_TITLE_PROPERTY_KEY,
                    FilterOperator.GREATER_THAN_OR_EQUAL, title.toLowerCase()),
                new FilterPredicate(LOWERCASE_TITLE_PROPERTY_KEY, FilterOperator.LESS_THAN,
                    NextIncreasingLexicographicStringUtility.getNextIncreasingLexicographicString(
                        title.toLowerCase())))));
  }

  private static List<EntertainmentItem> getListFromQuery(Query query) {
    PreparedQuery queryResults = datastoreService.prepare(query);

    List<EntertainmentItem> entertainmentItemList = new ArrayList<>();

    for (Entity entertainmentItemEntity : queryResults.asIterable()) {
      String title = (String) entertainmentItemEntity.getProperty(TITLE_PROPERTY_KEY);
      String description = (String) entertainmentItemEntity.getProperty(DESCRIPTION_PROPERTY_KEY);
      String imageURL = (String) entertainmentItemEntity.getProperty(IMAGE_URL_PROPERTY_KEY);

      entertainmentItemList.add(new EntertainmentItem(title, description, imageURL));
    }

    return entertainmentItemList;
  }

  private EntertainmentItemDatastore() {}
}
