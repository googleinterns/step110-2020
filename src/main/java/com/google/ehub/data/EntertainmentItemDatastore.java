package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.ehub.utility.QueryUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Singleton class that manages EntertainmentItems stored in Datastore.
 */
public final class EntertainmentItemDatastore {
  private static final String ENTERTAINMENT_ITEM_KIND = "entertainmentItem";
  private static final String DISPLAY_TITLE_PROPERTY_KEY = "displayTitle";
  private static final String NORMALIZED_TITLE_PROPERTY_KEY = "normalizedTitle";
  private static final String DESCRIPTION_PROPERTY_KEY = "description";
  private static final String IMAGE_URL_PROPERTY_KEY = "imageURL";

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
   * @param entertainmentItem entertainment item to add into Datastore
   */
  public void addItemToDatastore(EntertainmentItem entertainmentItem) {
    Entity entertainmentItemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);

    // Unique Id is created by Datastore so it's not added as a property.
    entertainmentItemEntity.setProperty(DISPLAY_TITLE_PROPERTY_KEY, entertainmentItem.getTitle());
    entertainmentItemEntity.setProperty(
        NORMALIZED_TITLE_PROPERTY_KEY, entertainmentItem.getTitle().toLowerCase());
    entertainmentItemEntity.setProperty(
        DESCRIPTION_PROPERTY_KEY, entertainmentItem.getDescription());
    entertainmentItemEntity.setProperty(IMAGE_URL_PROPERTY_KEY, entertainmentItem.getImageURL());

    datastoreService.put(entertainmentItemEntity);
  }

  /**
   * Finds a single entertainment item based on unique id.
   *
   * @param uniqueId id used to identify the EntertainmentItem Entity in the Datastore
   * @return the EntertainmentItem found in Datastore wrapped in an {@link Optional}, the
   * optional object will be empty if the EntertainmentItem Entity was not found
   */
  public Optional<EntertainmentItem> queryItem(long uniqueId) {
    Query query =
        new Query(ENTERTAINMENT_ITEM_KIND)
            .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL,
                KeyFactory.createKey(ENTERTAINMENT_ITEM_KIND, uniqueId)));
    PreparedQuery queryResults = datastoreService.prepare(query);

    Entity entertainmentItemEntity = queryResults.asSingleEntity();

    if (entertainmentItemEntity == null) {
      return Optional.empty();
    }

    return Optional.of(createItemFromEntity(entertainmentItemEntity));
  }

  /**
   * Queries all entertainment items found in Datastore.
   *
   * @return list with all entertainment items found in Datastore, the list will be empty if no
   *     items were found
   */
  public List<EntertainmentItem> queryAllItems() {
    return createListFromQuery(new Query(ENTERTAINMENT_ITEM_KIND));
  }

  /**
   * Queries all entertainment items found in Datastore and orders them based on title with the
   * given sorting direction.
   *
   * @param sortDirection the sort direction used to order the entertainment items based on title
   * @return list with all the entertainment items following the specified sort direction, the list
   *     will be empty if no items were found
   */
  public List<EntertainmentItem> queryAllItemsWithTitleOrder(SortDirection sortDirection) {
    return createListFromQuery(
        new Query(ENTERTAINMENT_ITEM_KIND).addSort(NORMALIZED_TITLE_PROPERTY_KEY, sortDirection));
  }

  /**
   * Queries entertainment items with the specified title prefix and sorting direction.
   *
   * @param title the title prefix used to filter the query
   * @param sortDirection the sort direction used to order the entertainment items based on title
   * @return list with the entertainment items that match the title prefix and sorting direction,
   *     the list will be empty if no items were found
   */
  public List<EntertainmentItem> queryItemsByTitlePrefix(
      String title, SortDirection sortDirection) {
    return createListFromQuery(new Query(ENTERTAINMENT_ITEM_KIND)
                                   .addSort(NORMALIZED_TITLE_PROPERTY_KEY, sortDirection)
                                   .setFilter(QueryUtility.getPrefixFilter(
                                       NORMALIZED_TITLE_PROPERTY_KEY, title.toLowerCase())));
  }

  private List<EntertainmentItem> createListFromQuery(Query query) {
    PreparedQuery queryResults = datastoreService.prepare(query);

    List<EntertainmentItem> entertainmentItemList = new ArrayList<>();

    for (Entity entertainmentItemEntity : queryResults.asIterable()) {
      entertainmentItemList.add(createItemFromEntity(entertainmentItemEntity));
    }

    return entertainmentItemList;
  }

  private static EntertainmentItem createItemFromEntity(Entity entertainmentItemEntity) {
    long uniqueId = entertainmentItemEntity.getKey().getId();
    String title = (String) entertainmentItemEntity.getProperty(DISPLAY_TITLE_PROPERTY_KEY);
    String description = (String) entertainmentItemEntity.getProperty(DESCRIPTION_PROPERTY_KEY);
    String imageURL = (String) entertainmentItemEntity.getProperty(IMAGE_URL_PROPERTY_KEY);

    return new EntertainmentItem(uniqueId, title, description, imageURL);
  }
}
