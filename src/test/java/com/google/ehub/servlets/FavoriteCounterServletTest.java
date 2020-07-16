package com.google.ehub.servlets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
public class FavoriteCounterServletTest {
  private static final String ITEM_ID_PARAMETER_KEY = "itemId";
  private static final String JSON_CONTENT_TYPE = "application/json";

  private static final String VALID_ITEM_ID_PARAMETER = "123";
  private static final String NON_PARSABLE_ITEM_ID_PARAMETER = "jwnfnziwo";

  private static final String[] EMAILS = {
      "Bryan@gmail.com", "jak@gmail.com", "test@outlook.com", "newEmail@yahoo.com"};

  private static final Long VALID_ITEM_ID = 123L;

  private final FavoriteCounterServlet servlet = new FavoriteCounterServlet();
  private final FavoriteItemDatastore favoriteItemDatastore = FavoriteItemDatastore.getInstance();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

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
  public void getRequestWithNullParam_errorIsSent() throws IOException {
    when(request.getParameter(ITEM_ID_PARAMETER_KEY)).thenReturn(null);

    servlet.doGet(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void getRequestWithEmptyParam_errorIsSent() throws IOException {
    when(request.getParameter(ITEM_ID_PARAMETER_KEY)).thenReturn("");

    servlet.doGet(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void getRequestWithNonParsableParam_errorIsSent() throws IOException {
    when(request.getParameter(ITEM_ID_PARAMETER_KEY)).thenReturn(NON_PARSABLE_ITEM_ID_PARAMETER);

    servlet.doGet(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
  }

  @Test
  public void getRequestWithValidParamOfItemWithoutLikes_responseSendsEmptyEmailList()
      throws IOException {
    when(request.getParameter(ITEM_ID_PARAMETER_KEY)).thenReturn(VALID_ITEM_ID_PARAMETER);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println("0");
  }

  @Test
  public void getRequestWithValidParamOfItemWithLikes_responseSendsCompleteEmailList()
      throws IOException {
    for (String email : EMAILS) {
      favoriteItemDatastore.addFavoriteItem(email, VALID_ITEM_ID);
    }

    when(request.getParameter(ITEM_ID_PARAMETER_KEY)).thenReturn(VALID_ITEM_ID_PARAMETER);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType(JSON_CONTENT_TYPE);
    verify(printWriter).println(new Gson().toJson(EMAILS.length));
  }
}
