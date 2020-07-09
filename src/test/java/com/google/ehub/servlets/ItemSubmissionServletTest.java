package com.google.ehub.servlets;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.data.EntertainmentItemDatastore;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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
public class ItemSubmissionServletTest {
  private final ItemSubmissionServlet servlet = new ItemSubmissionServlet();
  private final EntertainmentItemDatastore entertainmentItemDatastore =
      EntertainmentItemDatastore.getInstance();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private static final String TITLE_PARAMETER_KEY = "Title";
  private static final String DESCRIPTION_PARAMETER_KEY = "Plot";
  private static final String IMAGE_URL_PARAMETER_KEY = "Poster";
  private static final String RELEASE_DATE_PARAMETER_KEY = "Released";
  private static final String RUNTIME_PARAMETER_KEY = "Runtime";
  private static final String GENRE_PARAMETER_KEY = "Genre";
  private static final String DIRECTORS_PARAMETER_KEY = "Director";
  private static final String WRITERS_PARAMETER_KEY = "Writer";
  private static final String ACTORS_PARAMETER_KEY = "Actors";
  private static final String IMDB_ID_PARAMETER_KEY = "imdbID";

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

  private static final String JSON_CONTENT_TYPE = "application/json";

  private static final int MAX_TITLE_CHARS = 150;
  private static final int MAX_CHARS = 500;

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
  public void postRequestWithNullParams_NoItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(RELEASE_DATE_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(RUNTIME_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(GENRE_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(DIRECTORS_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(WRITERS_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(ACTORS_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(null);

    servlet.doPost(request, response);

    Assert.assertEquals(0,
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults())
            .getItems()
            .size());
  }

  @Test
  public void postRequestWithValidParams_ItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn(TITLE);
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn(DESCRIPTION);
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn(IMAGE_URL);
    when(request.getParameter(RELEASE_DATE_PARAMETER_KEY)).thenReturn(RELEASE_DATE);
    when(request.getParameter(RUNTIME_PARAMETER_KEY)).thenReturn(RUNTIME);
    when(request.getParameter(GENRE_PARAMETER_KEY)).thenReturn(GENRE);
    when(request.getParameter(DIRECTORS_PARAMETER_KEY)).thenReturn(DIRECTORS);
    when(request.getParameter(WRITERS_PARAMETER_KEY)).thenReturn(WRITERS);
    when(request.getParameter(ACTORS_PARAMETER_KEY)).thenReturn(ACTORS);
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(OMDB_ID);

    servlet.doPost(request, response);

    Assert.assertEquals(1,
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults())
            .getItems()
            .size());
  }

  @Test
  public void postRequestsWithEmptyParams_NoItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(RELEASE_DATE_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(RUNTIME_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(GENRE_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(DIRECTORS_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(WRITERS_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(ACTORS_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn("");

    servlet.doPost(request, response);

    Assert.assertEquals(0,
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults())
            .getItems()
            .size());
  }

  @Test
  public void postRequestWithExceedingTitleLength_NoItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY))
        .thenReturn(getStringOfLength(MAX_TITLE_CHARS + 1));
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn(DESCRIPTION);
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn(IMAGE_URL);
    when(request.getParameter(RELEASE_DATE_PARAMETER_KEY)).thenReturn(RELEASE_DATE);
    when(request.getParameter(RUNTIME_PARAMETER_KEY)).thenReturn(RUNTIME);
    when(request.getParameter(GENRE_PARAMETER_KEY)).thenReturn(GENRE);
    when(request.getParameter(DIRECTORS_PARAMETER_KEY)).thenReturn(DIRECTORS);
    when(request.getParameter(WRITERS_PARAMETER_KEY)).thenReturn(WRITERS);
    when(request.getParameter(ACTORS_PARAMETER_KEY)).thenReturn(ACTORS);
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(OMDB_ID);

    servlet.doPost(request, response);

    Assert.assertEquals(0,
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults())
            .getItems()
            .size());
  }

  @Test
  public void postRequestWithMaximumValidLengthParams_ItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_TITLE_CHARS));
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_CHARS));
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_CHARS));
    when(request.getParameter(RELEASE_DATE_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_CHARS));
    when(request.getParameter(RUNTIME_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_CHARS));
    when(request.getParameter(GENRE_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_CHARS));
    when(request.getParameter(DIRECTORS_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_CHARS));
    when(request.getParameter(WRITERS_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_CHARS));
    when(request.getParameter(ACTORS_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_CHARS));
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(OMDB_ID);

    servlet.doPost(request, response);

    Assert.assertEquals(1,
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults())
            .getItems()
            .size());
  }

  @Test
  public void postRequestWithMixOfValidAndInvalidParams_NoItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn(TITLE);
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(RELEASE_DATE_PARAMETER_KEY)).thenReturn(RELEASE_DATE);
    when(request.getParameter(RUNTIME_PARAMETER_KEY)).thenReturn(RUNTIME);
    when(request.getParameter(GENRE_PARAMETER_KEY)).thenReturn(GENRE);
    when(request.getParameter(DIRECTORS_PARAMETER_KEY)).thenReturn(DIRECTORS);
    when(request.getParameter(WRITERS_PARAMETER_KEY)).thenReturn(WRITERS);
    when(request.getParameter(ACTORS_PARAMETER_KEY)).thenReturn(ACTORS);
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(OMDB_ID);

    servlet.doPost(request, response);

    Assert.assertEquals(0,
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults())
            .getItems()
            .size());
  }

  @Test
  public void postRequestWithDuplicateItem_NoItemIsAdded() throws IOException {
    entertainmentItemDatastore.addItemToDatastore(new EntertainmentItem.Builder()
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
                                                      .build());

    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn(TITLE);
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn(DESCRIPTION);
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn(IMAGE_URL);
    when(request.getParameter(RELEASE_DATE_PARAMETER_KEY)).thenReturn(RELEASE_DATE);
    when(request.getParameter(RUNTIME_PARAMETER_KEY)).thenReturn(RUNTIME);
    when(request.getParameter(GENRE_PARAMETER_KEY)).thenReturn(GENRE);
    when(request.getParameter(DIRECTORS_PARAMETER_KEY)).thenReturn(DIRECTORS);
    when(request.getParameter(WRITERS_PARAMETER_KEY)).thenReturn(WRITERS);
    when(request.getParameter(ACTORS_PARAMETER_KEY)).thenReturn(ACTORS);
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(OMDB_ID);

    servlet.doPost(request, response);

    Assert.assertEquals(1,
        entertainmentItemDatastore.queryAllItems(FetchOptions.Builder.withDefaults())
            .getItems()
            .size());
  }

  @Test
  public void getRequestWithNullParam_NoResponseIsSent() throws IOException {
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(null);

    servlet.doGet(request, response);

    verify(response, never()).setContentType(JSON_CONTENT_TYPE);
  }

  @Test
  public void getRequestWithEmptyParam_NoResponseIsSent() throws IOException {
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn("");

    servlet.doGet(request, response);

    verify(response, never()).setContentType(JSON_CONTENT_TYPE);
  }

  @Test
  public void getRequestWithUniqueItem_ResponseReturnsItemIsUnique() throws IOException {
    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(OMDB_ID);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println(new Gson().toJson(/* isItemUnique */ true));
  }

  @Test
  public void getRequestWithDuplicateItem_ResponseReturnsItemIsNotUnique() throws IOException {
    entertainmentItemDatastore.addItemToDatastore(new EntertainmentItem.Builder()
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
                                                      .build());

    when(request.getParameter(IMDB_ID_PARAMETER_KEY)).thenReturn(OMDB_ID);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println(new Gson().toJson(/* isItemUnique */ false));
  }

  private static String getStringOfLength(int characterLength) {
    char[] chars = new char[characterLength];
    Arrays.fill(chars, 'a');
    return new String(chars);
  }
}
