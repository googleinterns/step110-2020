package com.google.ehub.servlets;

import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.ehub.data.EntertainmentItemDatastore;
import java.io.IOException;
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

  private static final int MAX_TITLE_CHARS = 150;
  private static final int MAX_DESCRIPTION_CHARS = 500;

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
  public void postRequestWithNullParams_NoItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn(null);

    servlet.doPost(request, response);

    Assert.assertEquals(0, entertainmentItemDatastore.queryAllItems().size());
  }

  @Test
  public void postRequestWithValidParams_ItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn("Star Wars");
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn("Galaxy Far Far Away");
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn("SamplePoster.png");

    servlet.doPost(request, response);

    Assert.assertEquals(1, entertainmentItemDatastore.queryAllItems().size());
  }

  @Test
  public void postRequestsWithEmptyParams_NoItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn("");
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn("");

    servlet.doPost(request, response);

    Assert.assertEquals(0, entertainmentItemDatastore.queryAllItems().size());
  }

  @Test
  public void postRequestWithExceedingTitleLength_NoItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY))
        .thenReturn(getStringOfLength(MAX_TITLE_CHARS + 1));
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn("Random Description");
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn("CoolImage.jpg");

    servlet.doPost(request, response);

    Assert.assertEquals(0, entertainmentItemDatastore.queryAllItems().size());
  }

  @Test
  public void postRequestWithMaximumValidLengthParams_ItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn(getStringOfLength(MAX_TITLE_CHARS));
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY))
        .thenReturn(getStringOfLength(MAX_DESCRIPTION_CHARS));
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn("CoolImage.jpg");

    servlet.doPost(request, response);

    Assert.assertEquals(1, entertainmentItemDatastore.queryAllItems().size());
  }

  @Test
  public void postRequestWithMixOfValidAndInvalidParams_NoItemIsAdded() throws IOException {
    when(request.getParameter(TITLE_PARAMETER_KEY)).thenReturn("Valid Title");
    when(request.getParameter(DESCRIPTION_PARAMETER_KEY)).thenReturn(null);
    when(request.getParameter(IMAGE_URL_PARAMETER_KEY)).thenReturn("");

    servlet.doPost(request, response);

    Assert.assertEquals(0, entertainmentItemDatastore.queryAllItems().size());
  }

  private static String getStringOfLength(int characterLength) {
    char[] chars = new char[characterLength];
    Arrays.fill(chars, 'a');
    return new String(chars);
  }
}
