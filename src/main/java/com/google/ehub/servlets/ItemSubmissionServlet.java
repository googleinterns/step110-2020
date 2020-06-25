package com.google.ehub.servlets;

import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.data.EntertainmentItemDatastore;
import com.google.ehub.utility.BlobstoreURLUtility;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles POST requests to add entertainment items into Datastore.
 */
@WebServlet("/item-submission")
public class ItemSubmissionServlet extends HttpServlet {
  private static final String TITLE_PARAMETER_KEY = "title";
  private static final String DESCRIPTION_PARAMETER_KEY = "description";
  private static final String IMAGE_URL_PARAMETER_KEY = "imageURL";

  private static final int MAX_TITLE_CHARS = 64;
  private static final int MAX_DESCRIPTION_CHARS = 2048;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String title = request.getParameter(TITLE_PARAMETER_KEY);
    String description = request.getParameter(DESCRIPTION_PARAMETER_KEY);
    Optional<String> imageURL = BlobstoreURLUtility.getUploadURL(request, IMAGE_URL_PARAMETER_KEY);

    if (!arePostRequestParametersValid(title, description, imageURL)) {
      System.err.println("ItemSubmissionServlet: Post Request parameters not specified!");
      return;
    }

    EntertainmentItemDatastore.getInstance().addItemToDatastore(
        EntertainmentItem.unassignedItem(title, description, imageURL.get()));

    response.sendRedirect("/index.html");
  }

  /**
   * Verifies if the parameters given in the HTTP Post Request are valid.
   *
   * @param title the title given in the Post request parameter
   * @param description the description given in the Post request parameter
   * @param imageURL the image URL given by Blobstore wrapped in an {@link Optional}
   * @return true if the parameters given in the Post request are valid, false otherwise
   */
  private static boolean arePostRequestParametersValid(
      String title, String description, Optional<String> imageURL) {
    return (title != null && !title.isEmpty() && title.length() <= MAX_TITLE_CHARS)
        && (description != null && !description.isEmpty())
        && (imageURL != null && imageURL.isPresent());
  }
}
