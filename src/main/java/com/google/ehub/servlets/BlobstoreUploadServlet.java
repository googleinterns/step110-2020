package com.google.ehub.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles GET requests for upload Url from Blobstore API.
 */
@WebServlet("/blobstore-upload")
public class BlobstoreUploadServlet extends HttpServlet {
  private static String SERVLET_REDIRECT_URL_PARAMETER_KEY = "servletRedirectUrl";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String servletRedirectUrl = request.getParameter(SERVLET_REDIRECT_URL_PARAMETER_KEY);

    if (servletRedirectUrl == null || servletRedirectUrl.isEmpty()) {
      System.err.println("BlobstoreUploadServlet: No servlet redirect Url specified!");
      return;
    }

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl(servletRedirectUrl);

    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }
}
