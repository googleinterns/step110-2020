// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.ehub.data.CommentData;
import com.google.ehub.data.CommentDataManager;
import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.data.EntertainmentItemDatastore;
import com.google.ehub.data.ItemPageData;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that compiles the data for the item page*/
@WebServlet("/itempagedata")
public class ItemPageServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long itemId = Long.parseLong(request.getParameter("itemId"));

    Optional<EntertainmentItem> optionalItem =
        EntertainmentItemDatastore.getInstance().queryItem(itemId);

    CommentDataManager commentDataManager = new CommentDataManager();
    List<CommentData> comments = commentDataManager.retrieveComments(itemId);

    if (optionalItem.isPresent()) {
      EntertainmentItem selectedItem = optionalItem.get();
      ItemPageData itemData = new ItemPageData(selectedItem, comments);
      response.setContentType("application/json");
      response.getWriter().println(new Gson().toJson(itemData));
    } else {
      System.out.println("ItemPageServlet: itemData is absent");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long itemId;
    try {
      itemId = Long.parseLong(request.getParameter(CommentDataManager.ITEM_ID_PROPERTY_KEY));
    } catch (NumberFormatException e) {
      System.err.println("Can't parse itemId to a long");
      return;
    }
    String comment = request.getParameter(CommentDataManager.COMMENT_PROPERTY_KEY);
    if (comment == null) {
      System.out.println("Comment was not entered.");
      return;
    }
    long timestampMillis = System.currentTimeMillis();
    CommentDataManager comments = new CommentDataManager();
    comments.addItemComment(itemId, comment, timestampMillis);
  }
}
