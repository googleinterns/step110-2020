package com.google.ehub.servlets;

import static org.junit.Assert.assertFalse; 
import static org.junit.Assert.assertNull;
import static org.junit.Assert.*; 

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
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static  String LOGOUT_URL = "/logout";
  private static  String LOGIN_URL = "/login";
  private static final String userId = "12345";
  private static final String email = "abc@gmail.com";
  private static final String authDomain = "gmail.com";
  private static final User user= new User(email, authDomain , userId);
  private static final String ERROR_MESSAGE = "Error Message"; 
 
  private final LoginServlet servlet = new LoginServlet();
 
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;
  @Mock PrintWriter printWriter;

  UserService userService = UserServiceFactory.getUserService();

  private final LocalServiceTestHelper helper =new LocalServiceTestHelper(new LocalUserServiceTestConfig()).setEnvEmail("test@gmail.com")
          .setEnvIsLoggedIn(true)
          .setEnvAuthDomain("gmail.com");

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

    servlet.doGet(request, response);

    String responseStr = stringWriter.getBuffer().toString().trim();
    JsonElement responseJsonElement = new JsonParser().parse(responseStr);
    JsonObject responseJsonObject = responseJsonElement.getAsJsonObject();
    
    return responseJsonObject;
  }

  @Test
  public void getRequestWithLoggedInUser_validContentGetsSent() throws IOException {
    JsonObject responseJsonObject = getLoginServletResponse();

    LOGIN_URL = responseJsonObject.get("LoginURL").getAsString();
    LOGOUT_URL = responseJsonObject.get("LogoutURL").getAsString();

    Assert.assertTrue(LOGOUT_URL.contains("logout"));
    Assert.assertTrue(LOGIN_URL.isEmpty());
  }
 
  @Test
  public void getRequestWithLoggedOut_validContentGetsSent() throws IOException {
    helper.setEnvIsLoggedIn(false);
    
    JsonObject responseJsonObject = getLoginServletResponse();

    LOGIN_URL = responseJsonObject.get("LoginURL").getAsString();
    LOGOUT_URL = responseJsonObject.get("LogoutURL").getAsString();

    Assert.assertTrue(LOGIN_URL.contains("login"));
    Assert.assertTrue(LOGOUT_URL.isEmpty());
  }
}
