package com.google.ehub.data;

import java.util.List;

/**
 * Stores data used by DashboardServlet to send information about the list of
 * Entertainment Items and the current page cursor.
 */
public final class EntertainmentItemList {
  private final List<EntertainmentItem> items;
  private final String pageCursor;

  public EntertainmentItemList(List<EntertainmentItem> items, String pageCursor) {
    this.items = items;
    this.pageCursor = pageCursor;
  }

  public List<EntertainmentItem> getItems() {
    return items;
  }

  public String getPageCursor() {
    return pageCursor;
  }
}
