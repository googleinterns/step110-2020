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
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.ehub.data.CommentData;
import com.google.ehub.data.CommentDataManager;
import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.data.EntertainmentItemDatastore;
import com.google.ehub.data.ItemPageData;
import com.google.ehub.servlets.ItemPageServlet;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
  private final ItemPageServlet servlet = new ItemPageServlet();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final String JSON_CONTENT_TYPE = "application/json";

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

  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;
  @Mock PrintWriter printWriter;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGETReturnsItemPageData() throws IOException {
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
    CommentDataManager commentDataManager = new CommentDataManager();
    commentDataManager.addItemComment(itemId.getId(), "Nice", 1093923L);

    servlet.doGet(request, response);
    CommentData comment = new CommentData(itemId.getId(), "Nice", 1093923L);
    List<CommentData> comments = new ArrayList<>();
    comments.add(comment);
    Optional<EntertainmentItem> expectedItem =
        EntertainmentItemDatastore.getInstance().queryItem(itemId.getId());
    ItemPageData itemData = new ItemPageData(expectedItem.get(), comments);

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println(new Gson().toJson(itemData));
  }

  @Test
  public void doGETMissingEntertainmentItem_ReturnsError() throws IOException {
    when(request.getParameter("itemId")).thenReturn("102930");
    servlet.doGet(request, response);
    verify(response).sendError(anyInt(), any());
  }

  @Test
  public void doPOSTAddsValidComment() throws IOException {
    when(request.getParameter("itemId")).thenReturn("12345");
    CommentDataManager commentDataManager = new CommentDataManager();
    commentDataManager.addItemComment(12345, "Nice", 1093923L);
    servlet.doPost(request, response);
    Assert.assertEquals(1, commentDataManager.retrieveComments(12345).size());
  }

  @Test
  public void doPOSTDoesNotAddComment_NoValidComment() throws IOException {
    when(request.getParameter("itemId")).thenReturn("12345");
    CommentDataManager commentDataManager = new CommentDataManager();
    servlet.doPost(request, response);
    Assert.assertEquals(0, commentDataManager.retrieveComments(12345).size());
  }
}