package com.google.ehub.servlets;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.ehub.data.EntertainmentItemDatastore;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
public class DashboardServletTest {
  private final DashboardServlet servlet = new DashboardServlet();
  private final PrintWriter printWriter = new PrintWriter(new StringWriter());
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private static final String SEARCH_VALUE_PARAMETER_KEY = "searchValue";
  private static final String SORTING_DIRECTION_PARAMETER_KEY = "sortingDirection";
  private static final String ASCENDING_PARAMETER_VALUE = "ASCENDING";
  private static final String JSON_CONTENT_TYPE = "application/json";

  private static final int MAX_SEARCH_VALUE_CHARS = 150;

  @Mock HttpServletRequest request;

  @Mock HttpServletResponse response;

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
  public void getRequestWithNullParams_NoContentGetsSent() throws IOException {
    when(request.getParameter(SEARCH_VALUE_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(SORTING_DIRECTION_PARAMETER_KEY)).thenReturn(null);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response, never()).setContentType(JSON_CONTENT_TYPE);
  }

  @Test
  public void getRequestWithEmptyParams_NoContentGetsSent() throws IOException {
    when(request.getParameter(SEARCH_VALUE_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(SORTING_DIRECTION_PARAMETER_KEY)).thenReturn("");
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response, never()).setContentType(JSON_CONTENT_TYPE);
  }

  @Test
  public void getRequestWithValidParams_ContentGetsSent() throws IOException {
    when(request.getParameter(SEARCH_VALUE_PARAMETER_KEY)).thenReturn("Star Wars");
    when(request.getParameter(SORTING_DIRECTION_PARAMETER_KEY))
        .thenReturn(ASCENDING_PARAMETER_VALUE);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType(JSON_CONTENT_TYPE);
  }

  @Test
  public void getRequestWithInvalidSortingParam_NoContentGetsSent() throws IOException {
    when(request.getParameter(SEARCH_VALUE_PARAMETER_KEY)).thenReturn("Star Wars");
    when(request.getParameter(SORTING_DIRECTION_PARAMETER_KEY)).thenReturn("Invalid Sorting");
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response, never()).setContentType(JSON_CONTENT_TYPE);
  }

  @Test
  public void getRequestWithExceedingSearchValueLength_NoContentGetsSent() throws IOException {
    char[] searchValueChars = new char[500];
    Arrays.fill(searchValueChars, 'a');
    String largeSearchValue = new String(searchValueChars);

    when(request.getParameter(SEARCH_VALUE_PARAMETER_KEY)).thenReturn(largeSearchValue);
    when(request.getParameter(SORTING_DIRECTION_PARAMETER_KEY)).thenReturn("Invalid Sorting");
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response, never()).setContentType(JSON_CONTENT_TYPE);
  }

  @Test
  public void getRequestWithMaximumValidSearchValueLength_ContentGetsSent() throws IOException {
    char[] searchValueChars = new char[MAX_SEARCH_VALUE_CHARS];
    Arrays.fill(searchValueChars, 'a');
    String maxValidSearchValue = new String(searchValueChars);

    when(request.getParameter(SEARCH_VALUE_PARAMETER_KEY)).thenReturn(maxValidSearchValue);
    when(request.getParameter(SORTING_DIRECTION_PARAMETER_KEY))
        .thenReturn(ASCENDING_PARAMETER_VALUE);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType(JSON_CONTENT_TYPE);
  }
}
