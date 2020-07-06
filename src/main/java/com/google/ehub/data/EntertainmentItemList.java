package com.google.ehub.data;

import java.util.ArrayList;
import java.util.List;

public final class EntertainmentItemList {
  private final List<EntertainmentItem> itemList;
  private final String pageCursor;

  public EntertainmentItemList() {
    itemList = new ArrayList<>();
    pageCursor = "";
  }

  public EntertainmentItemList(List<EntertainmentItem> itemList, String pageCursor) {
    this.itemList = itemList;
    this.pageCursor = pageCursor;
  }

  public List<EntertainmentItem> getItemList() {
    return itemList;
  }

  public String getPageCursor() {
    return pageCursor;
  }
}
