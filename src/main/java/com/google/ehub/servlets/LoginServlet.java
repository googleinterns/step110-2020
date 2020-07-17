package com.google.ehub.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the GET request from the user login page provided by the Users API.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  private static final String LOGOUT_URL_KEY = "LogoutURL";
  private static final String LOGIN_URL_KEY = "LoginURL";
  private static final String IS_USER_LOGGED_IN_KEY = "isUserLoggedIn";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    JsonObject loginJson = new JsonObject();

    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/?authuser=0");
      loginJson.addProperty(LOGOUT_URL_KEY, logoutUrl);
      loginJson.addProperty(LOGIN_URL_KEY, "");
    } else {
      String loginUrl = userService.createLoginURL("/ProfilePage.html");
      loginJson.addProperty(LOGIN_URL_KEY, loginUrl);
      loginJson.addProperty(LOGOUT_URL_KEY, "");
        
    }
    loginJson.addProperty(IS_USER_LOGGED_IN_KEY, userService.isUserLoggedIn());
    response.setContentType("application/json");
    response.getWriter().println(loginJson.toString());
  }
}
