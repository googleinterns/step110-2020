package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.ehub.data.ProfileDatastore;
import com.google.ehub.data.UserDataManager;
import com.google.ehub.data.UserProfile;
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

  ProfileDatastore profileData = new ProfileDatastore();
  UserProfile newUser;
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    ProfileDatastore data = new ProfileDatastore();
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String username = request.getParameter("username");
    String bio = request.getParameter("bio");
    UserDataManager manager = new UserDataManager();
    newUser = manager.getUserProfile(email);

     if (!isValidParameter(name, email, username, bio)) {
      System.err.println("ProfileServlet: Post Request parameters empty");
      return;
    }

    profileData.addUserProfileToDatastore(name, email, username, bio);
    response.sendRedirect("/CreateProfilePage.html");
  }

@Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LoginServlet login = new LoginServlet();
    
    String useremail = login.getEmail();
    

    response.setContentType("application/json");
    response.getWriter().println(convertToJson(newUser));
  }
  private String convertToJson(UserProfile profile) {
    return new Gson().toJson(profile);
  }
  
  private boolean isValidParameter(String name, String email, String username, String bio) {
    return (name !=null && !name.isEmpty() && email != null && !email.isEmpty() && username !=null && !username.isEmpty() && bio !=null && !bio.isEmpty());
  }
}
