package com.google.ehub.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles GET requests for upload URL from Blobstore API
 */
@WebServlet("/blobstore-upload")
public class BlobstoreUploadServlet extends HttpServlet {
  private static String SERVLET_REDIRECT_URL_PARAMETER_KEY = "servletRedirectURL";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String servletRedirectURL = request.getParameter(SERVLET_REDIRECT_URL_PARAMETER_KEY);

    if (servletRedirectURL == null || servletRedirectURL.isEmpty()) {
      System.err.println("BlobstoreUploadServlet: No servlet redirect URL specified!");
      return;
    }

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadURL = blobstoreService.createUploadUrl(servletRedirectURL);

    response.setContentType("text/html");
    response.getWriter().println(uploadURL);
  }
}
