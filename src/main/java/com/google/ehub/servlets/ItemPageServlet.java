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

package com.google.ehub.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.ehub.data.CommentData;
import com.google.ehub.data.CommentDataManager;
import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.data.EntertainmentItemDatastore;
import com.google.ehub.data.ItemPageData;
import com.google.ehub.data.ProfileDatastore;
import com.google.ehub.data.UserProfile;
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
  private final UserService userService = UserServiceFactory.getUserService();
  private final ProfileDatastore profileData = new ProfileDatastore();

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
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "ItemPageServlet: itemData is not present");
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
    if (!userService.isUserLoggedIn()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User must logged in");
    } else {
      String email = userService.getCurrentUser().getEmail();
      String comment = request.getParameter(CommentDataManager.COMMENT_PROPERTY_KEY);
      if (comment == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Comment was not entered.");
      } else {
        long timestampMillis = System.currentTimeMillis();
        CommentDataManager comments = new CommentDataManager();
        comments.addItemComment(itemId, comment, timestampMillis, email);
      }
    }
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long commentId;
    CommentDataManager comment = new CommentDataManager();
    try {
      commentId = Long.parseLong(request.getParameter("commentId"));
    } catch (NumberFormatException e) {
      System.err.println("Can't parse itemId to a long");
      return;
    }
    comment.deleteComment(commentId);
  }
}
