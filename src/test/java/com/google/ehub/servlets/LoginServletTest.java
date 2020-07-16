package com.google.ehub.servlets;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
public class LoginServletTest {
  private final LoginServlet servlet = new LoginServlet();
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig())
          .setEnvEmail("test@gmail.com")
          .setEnvIsLoggedIn(true)
          .setEnvAuthDomain("gmail.com");

  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;
  @Mock PrintWriter printWriter;

  @Before
  public void init() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
  }

  @After
  public void tearDown() throws IOException {
    helper.tearDown();
  }

  private JsonObject getLoginServletResponse() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    when(response.getWriter()).thenReturn(printWriter);

    servlet.doPost(request, response);

    String responseStr = stringWriter.getBuffer().toString().trim();
    JsonElement responseJsonElement = new JsonParser().parse(responseStr);
    JsonObject responseJsonObject = responseJsonElement.getAsJsonObject();
    
    return responseJsonObject;
  }

  @Test
  public void postRequestWithLoggedInUser_sendsLogoutUrl() throws IOException {
    JsonObject responseJsonObject = getLoginServletResponse();

    String loginUrl = responseJsonObject.get("LoginURL").getAsString();
    String logoutUrl = responseJsonObject.get("LogoutURL").getAsString();

    Assert.assertTrue(logoutUrl.contains("logout"));
    Assert.assertTrue(loginUrl.isEmpty());
  }

  @Test
  public void postRequestWithLoggedOut_sendsLoginUrl() throws IOException {
    helper.setEnvIsLoggedIn(false);

    JsonObject responseJsonObject = getLoginServletResponse();

    String loginUrl = responseJsonObject.get("LoginURL").getAsString();
    String logoutUrl = responseJsonObject.get("LogoutURL").getAsString();

    Assert.assertTrue(loginUrl.contains("login"));
    Assert.assertTrue(logoutUrl.isEmpty());
  }
}
