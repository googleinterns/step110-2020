package com.google.ehub.servlets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.ehub.data.FavoriteItemDatastore;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
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
public class FavoriteItemServletTest {
  private static final String FAVORITE_ITEM_ID_PARAMETER_KEY = "favoriteItemId";
  private static final String ENTERTAINMENT_ITEM_KIND = "entertainmentItem";
  private static final String JSON_CONTENT_TYPE = "application/json";

  private static final String EMAIL = "Bryan@gmail.com";
  private static final String VALID_ITEM_ID_PARAMETER = "123";
  private static final String INVALID_ITEM_ID_PARAMETER = "wjnrwwoiofwij";

  private static final Long[] ITEM_IDS = {23L, 44L, 77L, 89L, 94L, 21301L};

  private final FavoriteItemServlet servlet = new FavoriteItemServlet();
  private final FavoriteItemDatastore favoriteItemDatastore = FavoriteItemDatastore.getInstance();
  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
          .setEnvEmail(EMAIL)
          .setEnvIsLoggedIn(true)
          .setEnvAuthDomain("gmail.com");

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
  public void getRequestUserNotLoggedIn_errorIsSent() throws IOException {
    helper.setEnvIsLoggedIn(false);

    servlet.doGet(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void getRequestUserLoggedInWithNoFavoriteItems_responseSendsEmptyList()
      throws IOException {
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println("[]");
  }

  @Test
  public void getRequestUserLoggedInWithFavoriteItems_responseSendsListWithFavoriteItems()
      throws IOException {
    for (Long itemId : ITEM_IDS) {
      favoriteItemDatastore.addFavoriteItem(EMAIL, itemId);
    }

    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println(new Gson().toJson(favoriteItemDatastore.queryFavoriteIds(EMAIL)));
  }

  @Test
  public void postRequestWithNullParam_errorIsSent() throws IOException {
    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY)).thenReturn(null);

    servlet.doPost(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void postRequestWithEmptyParam_errorIsSent() throws IOException {
    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY)).thenReturn("");

    servlet.doPost(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void postRequestWithInvalidParam_errorIsSent() throws IOException {
    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY))
        .thenReturn(INVALID_ITEM_ID_PARAMETER);

    servlet.doPost(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void postRequestWithValidParamButItemDoesNotExist_errorIsSent() throws IOException {
    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY)).thenReturn(VALID_ITEM_ID_PARAMETER);

    servlet.doPost(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void postRequestWithValidParamAndItemExists_favoriteItemIsAdded() throws IOException {
    Entity itemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);
    datastoreService.put(itemEntity);

    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY))
        .thenReturn(String.valueOf(itemEntity.getKey().getId()));

    servlet.doPost(request, response);

    Assert.assertEquals(1, FavoriteItemDatastore.getInstance().queryFavoriteIds(EMAIL).size());
  }

  @Test
  public void postRequestWithValidParamButUserNotLoggedIn_favoriteItemIsNotAdded()
      throws IOException {
    Entity itemEntity = new Entity(ENTERTAINMENT_ITEM_KIND);
    datastoreService.put(itemEntity);

    helper.setEnvIsLoggedIn(false);

    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY))
        .thenReturn(String.valueOf(itemEntity.getKey().getId()));

    servlet.doPost(request, response);

    Assert.assertTrue(favoriteItemDatastore.queryEmails(itemEntity.getKey().getId()).isEmpty());
  }

  @Test
  public void deleteRequestWithNullParam_errorIsSent() throws IOException {
    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY)).thenReturn(null);

    servlet.doDelete(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void deleteRequestWithEmptyParam_errorIsSent() throws IOException {
    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY)).thenReturn("");

    servlet.doDelete(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void deleteRequestWithInvalidParam_errorIsSent() throws IOException {
    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY))
        .thenReturn(INVALID_ITEM_ID_PARAMETER);

    servlet.doDelete(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void deleteRequestWithValidParamAndUserIsNotLoggedIn_errorIsSent() throws IOException {
    helper.setEnvIsLoggedIn(false);

    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY)).thenReturn(VALID_ITEM_ID_PARAMETER);

    servlet.doDelete(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void deleteRequestWithValidParamAndUserIsLoggedIn_favoriteItemEntityIsDeleted()
      throws IOException {
    favoriteItemDatastore.addFavoriteItem(EMAIL, ITEM_IDS[0]);

    when(request.getParameter(FAVORITE_ITEM_ID_PARAMETER_KEY))
        .thenReturn(String.valueOf(ITEM_IDS[0]));

    servlet.doDelete(request, response);

    Assert.assertTrue(favoriteItemDatastore.queryEmails(ITEM_IDS[0]).isEmpty());
  }
}
