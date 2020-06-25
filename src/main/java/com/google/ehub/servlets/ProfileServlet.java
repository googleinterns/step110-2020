package com.google.sps.servlets;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns HTML that contains the user's profile information.**/
@WebServlet("/profile-data")
public class ProfileServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect("/ProfilePage.html");
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String user_email = userService.getCurrentUser().getEmail();
    UserProfile newUser = UserDataManager.getUserProfile(user_email);
    response.setContentType("application/json");
    response.getWriter().println(convertToJson(newUser));
  }
  private String convertToJson(UserProfile profile) {
    Gson gson = new Gson();
    return gson.toJson(profile);
  }
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
