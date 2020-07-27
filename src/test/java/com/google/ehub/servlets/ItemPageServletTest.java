package com.google.ehub.servlets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.Lists;
import com.google.ehub.data.CommentData;
import com.google.ehub.data.CommentDataManager;
import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.data.EntertainmentItemDatastore;
import com.google.ehub.data.ItemPageData;
import com.google.ehub.data.ProfileDatastore;
import com.google.ehub.servlets.ItemPageServlet;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Object.*;
import java.util.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class ItemPageServletTest {
  private static final String TITLE = "Star Wars";
  private static final String DESCRIPTION = "Blah....";
  private static final String IMAGE_URL = "Image.png";
  private static final String RELEASE_DATE = "09/26/1972";
  private static final String RUNTIME = "2 hours";
  private static final String GENRE = "Sci-Fi";
  private static final String DIRECTORS = "George Lucas";
  private static final String WRITERS = "George Lucas";
  private static final String ACTORS = "Mark Hamill, Harrison Ford";
  private static final String OMDB_ID = "tt23113212";
  private static final String COMMENT = "Nice";
  private static final Long TIMESTAMP = 1093923L;
  private static final String EMAIL = "eeirikannu@gmail.com";
  private static final String ALT_EMAIL = "airwreckeye@gmail.com";
  private static final String USERNAME = "AirwreckEye";
  private static final Boolean BELONGS_TO_USER = true;

  private final ItemPageServlet servlet = new ItemPageServlet();
  private final CommentDataManager commentDataManager = new CommentDataManager();
  private static final String JSON_CONTENT_TYPE = "application/json";

  private LocalServiceTestHelper helper = new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig(), new LocalUserServiceTestConfig())
                                              .setEnvEmail(EMAIL)
                                              .setEnvIsLoggedIn(true)
                                              .setEnvAuthDomain("gmail.com");

  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;
  @Mock PrintWriter printWriter;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    ProfileDatastore profile = new ProfileDatastore();
    profile.addUserProfileToDatastore("Eric", EMAIL, USERNAME, "Hey");
    helper.setUp();

  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGetReturnsItemPageData() throws IOException {
    EntertainmentItem selectedItem = new EntertainmentItem.Builder()
                                         .setTitle(TITLE)
                                         .setDescription(DESCRIPTION)
                                         .setImageUrl(IMAGE_URL)
                                         .setReleaseDate(RELEASE_DATE)
                                         .setRuntime(RUNTIME)
                                         .setGenre(GENRE)
                                         .setDirectors(DIRECTORS)
                                         .setWriters(WRITERS)
                                         .setActors(ACTORS)
                                         .setOmdbId(OMDB_ID)
                                         .build();
    Key itemId = EntertainmentItemDatastore.getInstance().addItemToDatastore(selectedItem);
    when(request.getParameter("itemId")).thenReturn(itemId.getId() + "");
    when(response.getWriter()).thenReturn(printWriter);
    Key commentId = commentDataManager.addItemComment(itemId.getId(), COMMENT, TIMESTAMP, EMAIL);
    servlet.doGet(request, response);
    CommentData comment = new CommentData(
        itemId.getId(), COMMENT, TIMESTAMP, USERNAME, commentId.getId(), BELONGS_TO_USER);
    Optional<EntertainmentItem> expectedItem =
        EntertainmentItemDatastore.getInstance().queryItem(itemId.getId());
    ItemPageData itemData = new ItemPageData(expectedItem.get(), Lists.newArrayList(comment));

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println(new Gson().toJson(itemData));
  }

  @Test
  public void doGetReturnsItemPageData_differentUser() throws IOException {
    EntertainmentItem selectedItem = new EntertainmentItem.Builder()
                                         .setTitle(TITLE)
                                         .setDescription(DESCRIPTION)
                                         .setImageUrl(IMAGE_URL)
                                         .setReleaseDate(RELEASE_DATE)
                                         .setRuntime(RUNTIME)
                                         .setGenre(GENRE)
                                         .setDirectors(DIRECTORS)
                                         .setWriters(WRITERS)
                                         .setActors(ACTORS)
                                         .setOmdbId(OMDB_ID)
                                         .build();
    Key itemId = EntertainmentItemDatastore.getInstance().addItemToDatastore(selectedItem);
    when(request.getParameter("itemId")).thenReturn(itemId.getId() + "");
    when(response.getWriter()).thenReturn(printWriter);
    Key commentId = commentDataManager.addItemComment(itemId.getId(), COMMENT, TIMESTAMP, ALT_EMAIL);
    servlet.doGet(request, response);
    CommentData comment = new CommentData(
        itemId.getId(), COMMENT, TIMESTAMP, USERNAME, commentId.getId(), belongsToUser);
    Optional<EntertainmentItem> expectedItem =
        EntertainmentItemDatastore.getInstance().queryItem(itemId.getId());
    ItemPageData itemData = new ItemPageData(expectedItem.get(), Lists.newArrayList(comment));
    CommentData test_comment = commentDataManager.retrieveComments(12345).get(0);
    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println(new Gson().toJson(itemData));
    Assert.assertEquals(false, test_comment.belongsToUser);
  }
  @Test
  public void doGetReturnsItemPageData_userNotLoggedIn() throws IOException {
    helper.setEnvIsLoggedIn(false);
    EntertainmentItem selectedItem = new EntertainmentItem.Builder()
                                         .setTitle(TITLE)
                                         .setDescription(DESCRIPTION)
                                         .setImageUrl(IMAGE_URL)
                                         .setReleaseDate(RELEASE_DATE)
                                         .setRuntime(RUNTIME)
                                         .setGenre(GENRE)
                                         .setDirectors(DIRECTORS)
                                         .setWriters(WRITERS)
                                         .setActors(ACTORS)
                                         .setOmdbId(OMDB_ID)
                                         .build();
    Key itemId = EntertainmentItemDatastore.getInstance().addItemToDatastore(selectedItem);
    when(request.getParameter("itemId")).thenReturn(itemId.getId() + "");
    when(response.getWriter()).thenReturn(printWriter);
    Key commentId = commentDataManager.addItemComment(itemId.getId(), COMMENT, TIMESTAMP, EMAIL);
    servlet.doGet(request, response);
    CommentData comment = new CommentData(
        itemId.getId(), COMMENT, TIMESTAMP, USERNAME, commentId.getId(), belongsToUser);
    Optional<EntertainmentItem> expectedItem =
        EntertainmentItemDatastore.getInstance().queryItem(itemId.getId());
    ItemPageData itemData = new ItemPageData(expectedItem.get(), Lists.newArrayList(comment));

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println(new Gson().toJson(itemData));
  }

  @Test
  public void doGetMissingEntertainmentItem_ReturnsError() throws IOException {
    when(request.getParameter("itemId")).thenReturn("102930");
    servlet.doGet(request, response);
    verify(response).sendError(anyInt(), any());
  }

  @Test
  public void doGetRetrievesCommentForLoggedOutUser() throws IOException {
    helper.setEnvIsLoggedIn(false);
    when(request.getParameter("itemId")).thenReturn("12345");
    commentDataManager.addItemComment(12345, COMMENT, TIMESTAMP, EMAIL);
    servlet.doGet(request, response);
    Assert.assertEquals(1, commentDataManager.retrieveComments(12345).size());
  }

  @Test
  public void doPostAddsValidComment() throws IOException {
    when(request.getParameter("itemId")).thenReturn("12345");
    commentDataManager.addItemComment(12345, COMMENT, TIMESTAMP, EMAIL);
    servlet.doPost(request, response);
    Assert.assertEquals(1, commentDataManager.retrieveComments(12345).size());
  }

  @Test
  public void doPostDoesNotAddComment_NoValidComment() throws IOException {
    when(request.getParameter("itemId")).thenReturn("12345");
    servlet.doPost(request, response);
    Assert.assertEquals(0, commentDataManager.retrieveComments(12345).size());
  }
}
