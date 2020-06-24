package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.ehub.utility.NextIncreasingLexicographicStringUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Singleton class that manages EntertainmentItems stored in Datastore.
 */
public final class EntertainmentItemDatastore {
  private final String ENTERTAINMENT_ITEM_KIND = "entertainmentItem";
  private final String UNIQUE_ID_PROPERTY_KEY = "uniqueID";
  private final String TITLE_PROPERTY_KEY = "title";
  private final String LOWERCASE_TITLE_PROPERTY_KEY = "lowercaseTitle";
  private final String DESCRIPTION_PROPERTY_KEY = "description";
  private final String IMAGE_URL_PROPERTY_KEY = "imageURL";

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  private static EntertainmentItemDatastore instance;

  private EntertainmentItemDatastore() {}

  /**
   * Gives access to the single instance of the class, and creates this instance if it was not
   * initialized previously.
   *
   * @return single instance of the class
   */
  public static EntertainmentItemDatastore getInstance() {
    if (instance == null) {
      instance = new EntertainmentItemDatastore();
    }

    return instance;
  }

  /**
   * Adds an EntertainmentItem to Datastore.
   *
   * @param title title of the entertainment item
   * @param description description of the entertainment item
   * @param imageURL URL to the image of the entertainment item stored in Blobstore
   */
  public void addItemToDatastore(String title, String description, String imageURL) {
    Entity entertainmentItemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);

    entertainmentItemEntity.setProperty(TITLE_PROPERTY_KEY, title);
    entertainmentItemEntity.setProperty(LOWERCASE_TITLE_PROPERTY_KEY, title.toLowerCase());
    entertainmentItemEntity.setProperty(DESCRIPTION_PROPERTY_KEY, description);
    entertainmentItemEntity.setProperty(IMAGE_URL_PROPERTY_KEY, imageURL);

    datastoreService.put(entertainmentItemEntity);
  }

  /**
   * Finds a single entertainment item based on unique id.
   *
   * @param uniqueID id used to identify the EntertainmentItem Entity in the Datastore
   * @return the EntertainmentItem found in Datastore wrapped in an {@link Optional}, the
   * optional object will be empty if the EntertainmentItem Entity was not found
   */
  public Optional<EntertainmentItem> querySingleItem(long uniqueID) {
    Query query =
        new Query(ENTERTAINMENT_ITEM_KIND)
            .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL,
                KeyFactory.createKey(ENTERTAINMENT_ITEM_KIND, uniqueID)));
    PreparedQuery queryResults = datastoreService.prepare(query);

    Entity entertainmentItemEntity = queryResults.asSingleEntity();

    if (entertainmentItemEntity == null) {
      return Optional.empty();
    }

    return Optional.of(getItemFromEntity(entertainmentItemEntity));
  }

  /**
   * Queries all entertainment items found in Datastore.
   *
   * @return list with all entertainment items found in Datastore
   */
  public List<EntertainmentItem> queryAllEntertainmentItems() {
    return getListFromQuery(new Query(ENTERTAINMENT_ITEM_KIND));
  }

  /**
   * Queries all entertainment items found in Datastore and orders them based on title with the
   * given sorting direction.
   *
   * @param sortDirection the sort direction used to order the entertainment items based on title
   * @return list with all the entertainment items following the specified sort direction
   */
  public List<EntertainmentItem> queryAllItemsWithTitleOrder(SortDirection sortDirection) {
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
  public List<EntertainmentItem> queryItemsByTitlePrefix(
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

  private List<EntertainmentItem> getListFromQuery(Query query) {
    PreparedQuery queryResults = datastoreService.prepare(query);

    List<EntertainmentItem> entertainmentItemList = new ArrayList<>();

    for (Entity entertainmentItemEntity : queryResults.asIterable()) {
      entertainmentItemList.add(getItemFromEntity(entertainmentItemEntity));
    }

    return entertainmentItemList;
  }

  private EntertainmentItem getItemFromEntity(Entity entertainmentItemEntity) {
    long uniqueID = entertainmentItemEntity.getKey().getId();
    String title = (String) entertainmentItemEntity.getProperty(TITLE_PROPERTY_KEY);
    String description = (String) entertainmentItemEntity.getProperty(DESCRIPTION_PROPERTY_KEY);
    String imageURL = (String) entertainmentItemEntity.getProperty(IMAGE_URL_PROPERTY_KEY);

    return new EntertainmentItem(uniqueID, title, description, imageURL);
  }
}
