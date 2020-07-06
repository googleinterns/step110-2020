package com.google.ehub.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.ehub.utility.DatastoreUtils;
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
  private static final String IMAGE_URL_PROPERTY_KEY = "imageUrl";
  private static final String RELEASE_DATE_PROPERTY_KEY = "releaseDate";
  private static final String RUNTIME_PROPERTY_KEY = "runtime";
  private static final String GENRE_PROPERTY_KEY = "genre";
  private static final String DIRECTORS_PROPERTY_KEY = "directors";
  private static final String WRITERS_PROPERTY_KEY = "writers";
  private static final String ACTORS_PROPERTY_KEY = "actors";

  private static EntertainmentItemDatastore instance;

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

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
   * Adds an EntertainmentItem Entity to Datastore.
   *
   * @param item the EntertainmentItem being added to Datastore
   */
  public void addItemToDatastore(EntertainmentItem item) {
    Entity itemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);

    // Unique Id is created by Datastore so it's not added as a property.
    itemEntity.setProperty(DISPLAY_TITLE_PROPERTY_KEY, item.getTitle());
    itemEntity.setProperty(NORMALIZED_TITLE_PROPERTY_KEY, item.getTitle().toLowerCase());
    itemEntity.setProperty(DESCRIPTION_PROPERTY_KEY, item.getDescription());
    itemEntity.setProperty(IMAGE_URL_PROPERTY_KEY, item.getImageUrl());
    itemEntity.setProperty(RELEASE_DATE_PROPERTY_KEY, item.getReleaseDate());
    itemEntity.setProperty(RUNTIME_PROPERTY_KEY, item.getRuntime());
    itemEntity.setProperty(GENRE_PROPERTY_KEY, item.getGenre());
    itemEntity.setProperty(DIRECTORS_PROPERTY_KEY, item.getDirectors());
    itemEntity.setProperty(WRITERS_PROPERTY_KEY, item.getWriters());
    itemEntity.setProperty(ACTORS_PROPERTY_KEY, item.getActors());

    datastoreService.put(itemEntity);
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

    Entity itemEntity = queryResults.asSingleEntity();

    if (itemEntity == null) {
      return Optional.empty();
    }

    return Optional.of(createItemFromEntity(itemEntity));
  }

  /**
   * Queries all entertainment items found in Datastore.
   *
   * @param fetchOptions the fetch options used by the resulting query
   * @return list with all entertainment items found in Datastore, the list will be empty if no
   *     items were found
   */
  public EntertainmentItemList queryAllItems(FetchOptions fetchOptions) {
    return createItemListFromQuery(fetchOptions, new Query(ENTERTAINMENT_ITEM_KIND));
  }

  /**
   * Queries all entertainment items found in Datastore and orders them based on title with the
   * given sorting direction.
   *
   * @param fetchOptions the fetch options used by the resulting query
   * @param sortDirection the sort direction used to order the entertainment items based on title
   * @return list with all the entertainment items following the specified sort direction, the list
   *     will be empty if no items were found
   */
  public EntertainmentItemList queryAllItemsWithTitleOrder(
      FetchOptions fetchOptions, SortDirection sortDirection) {
    return createItemListFromQuery(fetchOptions,
        new Query(ENTERTAINMENT_ITEM_KIND).addSort(NORMALIZED_TITLE_PROPERTY_KEY, sortDirection));
  }

  /**
   * Queries entertainment items with the specified title prefix and sorting direction.
   *
   * @param fetchOptions the fetch options used by the resulting query
   * @param titlePrefix the title prefix used to filter the query
   * @param sortDirection the sort direction used to order the entertainment items based on title
   * @return list with the entertainment items that match the title prefix and sorting direction,
   *     the list will be empty if no items were found
   */
  public EntertainmentItemList queryItemsByTitlePrefix(
      FetchOptions fetchOptions, String titlePrefix, SortDirection sortDirection) {
    return createItemListFromQuery(fetchOptions,
        new Query(ENTERTAINMENT_ITEM_KIND)
            .addSort(NORMALIZED_TITLE_PROPERTY_KEY, sortDirection)
            .setFilter(DatastoreUtils.getPrefixFilter(
                NORMALIZED_TITLE_PROPERTY_KEY, titlePrefix.toLowerCase())));
  }

  private EntertainmentItemList createItemListFromQuery(FetchOptions fetchOptions, Query query) {
    PreparedQuery queryResults = datastoreService.prepare(query);

    QueryResultList<Entity> entityList = queryResults.asQueryResultList(fetchOptions);
    List<EntertainmentItem> itemList = new ArrayList<>();

    for (Entity itemEntity : entityList) {
      itemList.add(createItemFromEntity(itemEntity));
    }

    return new EntertainmentItemList(itemList, entityList.getCursor().toWebSafeString());
  }

  private static EntertainmentItem createItemFromEntity(Entity itemEntity) {
    Long uniqueId = itemEntity.getKey().getId();
    String title = (String) itemEntity.getProperty(DISPLAY_TITLE_PROPERTY_KEY);
    String description = (String) itemEntity.getProperty(DESCRIPTION_PROPERTY_KEY);
    String imageUrl = (String) itemEntity.getProperty(IMAGE_URL_PROPERTY_KEY);
    String releaseDate = (String) itemEntity.getProperty(RELEASE_DATE_PROPERTY_KEY);
    String runtime = (String) itemEntity.getProperty(RUNTIME_PROPERTY_KEY);
    String genre = (String) itemEntity.getProperty(GENRE_PROPERTY_KEY);
    String directors = (String) itemEntity.getProperty(DIRECTORS_PROPERTY_KEY);
    String writers = (String) itemEntity.getProperty(WRITERS_PROPERTY_KEY);
    String actors = (String) itemEntity.getProperty(ACTORS_PROPERTY_KEY);

    return new EntertainmentItem.Builder()
        .setUniqueId(uniqueId)
        .setTitle(title)
        .setDescription(description)
        .setImageUrl(imageUrl)
        .setReleaseDate(releaseDate)
        .setRuntime(runtime)
        .setGenre(genre)
        .setDirectors(directors)
        .setWriters(writers)
        .setActors(actors)
        .build();
  }
}
