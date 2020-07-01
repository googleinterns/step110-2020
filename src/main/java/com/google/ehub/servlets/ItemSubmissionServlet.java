package com.google.ehub.servlets;

import com.google.ehub.data.EntertainmentItem;
import com.google.ehub.data.EntertainmentItemDatastore;
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
  // The value for each key is capitalized because they follow the format of a JSON omdbItem.
  private static final String TITLE_PARAMETER_KEY = "Title";
  private static final String DESCRIPTION_PARAMETER_KEY = "Plot";
  private static final String IMAGE_URL_PARAMETER_KEY = "Poster";

  private static final int MAX_TITLE_CHARS = 150;
  private static final int MAX_DESCRIPTION_CHARS = 500;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String title = request.getParameter(TITLE_PARAMETER_KEY);
    String description = request.getParameter(DESCRIPTION_PARAMETER_KEY);
    String imageUrl = request.getParameter(IMAGE_URL_PARAMETER_KEY);

    if (!arePostRequestParametersValid(title, description, imageUrl)) {
      System.err.println("ItemSubmissionServlet: Post Request parameters not specified correctly!");
      return;
    }

    EntertainmentItemDatastore.getInstance().addItemToDatastore(
        new EntertainmentItem(0, title, description, imageUrl));

    response.sendRedirect("/index.html");
  }

  /**
   * Verifies if the parameters given in the HTTP Post Request are valid.
   *
   * @param title the title given in the Post request parameter
   * @param description the description given in the Post request parameter
   * @param imageUrl the image URL given in the Post request parameter
   * @return true if the parameters given in the Post request are valid, false otherwise
   */
  private static boolean arePostRequestParametersValid(
      String title, String description, String imageUrl) {
    return (title != null && !title.isEmpty() && title.length() <= MAX_TITLE_CHARS)
        && (description != null && !description.isEmpty()
            && description.length() <= MAX_DESCRIPTION_CHARS)
        && (imageUrl != null && !imageUrl.isEmpty());
  }
}
