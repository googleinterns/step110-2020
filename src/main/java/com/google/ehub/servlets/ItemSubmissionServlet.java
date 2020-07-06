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
  private static final String RELEASE_DATE_PARAMETER_KEY = "Released";
  private static final String RUNTIME_PARAMETER_KEY = "Runtime";
  private static final String GENRE_PARAMETER_KEY = "Genre";
  private static final String DIRECTORS_PARAMETER_KEY = "Director";
  private static final String WRITERS_PARAMETER_KEY = "Writer";
  private static final String ACTORS_PARAMETER_KEY = "Actors";

  private static final int MAX_TITLE_CHARS = 150;
  private static final int MAX_CHARS = 500;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String title = request.getParameter(TITLE_PARAMETER_KEY);
    String description = request.getParameter(DESCRIPTION_PARAMETER_KEY);
    String imageUrl = request.getParameter(IMAGE_URL_PARAMETER_KEY);
    String releaseDate = request.getParameter(RELEASE_DATE_PARAMETER_KEY);
    String runtime = request.getParameter(RUNTIME_PARAMETER_KEY);
    String genre = request.getParameter(GENRE_PARAMETER_KEY);
    String directors = request.getParameter(DIRECTORS_PARAMETER_KEY);
    String writers = request.getParameter(WRITERS_PARAMETER_KEY);
    String actors = request.getParameter(ACTORS_PARAMETER_KEY);

    if (!arePostRequestParametersValid(title, description, imageUrl, releaseDate, runtime, genre,
            directors, writers, actors)) {
      System.err.println("ItemSubmissionServlet: Post Request parameters not specified correctly!");
      return;
    }

    EntertainmentItemDatastore.getInstance().addItemToDatastore(new EntertainmentItem.Builder()
                                                                    .setTitle(title)
                                                                    .setDescription(description)
                                                                    .setImageUrl(imageUrl)
                                                                    .setReleaseDate(releaseDate)
                                                                    .setRuntime(runtime)
                                                                    .setGenre(genre)
                                                                    .setDirectors(directors)
                                                                    .setWriters(writers)
                                                                    .setActors(actors)
                                                                    .build());

    response.sendRedirect("/index.html");
  }

  /**
   * Verifies if the parameters given in the HTTP Post Request are valid.
   *
   * @param title the title given in the Post request parameter
   * @param description the description given in the Post request parameter
   * @param imageUrl the image URL given in the Post request parameter
   * @param runtime the runtime given in the Post request parameter
   * @param genre the genre given in the Post request parameter
   * @param directors the directors given in the Post request parameter
   * @param writers the writers given in the Post request parameter
   * @param actors the actors given in the Post request parameter
   * @return true if the parameters given in the Post request are valid, false otherwise
   */
  private static boolean arePostRequestParametersValid(String title, String description,
      String imageUrl, String releaseDate, String runtime, String genre, String directors,
      String writers, String actors) {
    return isParameterValid(title, MAX_TITLE_CHARS) && isParameterValid(description, MAX_CHARS)
        && isParameterValid(imageUrl, MAX_CHARS) && isParameterValid(releaseDate, MAX_CHARS)
        && isParameterValid(runtime, MAX_CHARS) && isParameterValid(genre, MAX_CHARS)
        && isParameterValid(directors, MAX_CHARS) && isParameterValid(writers, MAX_CHARS)
        && isParameterValid(actors, MAX_CHARS);
  }

  private static boolean isParameterValid(String parameter, int maxLength) {
    return parameter != null && !parameter.isEmpty() && parameter.length() <= maxLength;
  }
}
