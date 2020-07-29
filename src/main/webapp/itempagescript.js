/**
 * Retrieves ItemPageData and forms page using other functions.
 */
async function loadItemPage() {
  $(document).ready(function () {
    $("#navbar").load("navbar.html", function () {
      initializeNavBarProfileSection();
    });
    const itemId = getUrlParam("itemId");

    if (itemId !== "") {
      fetch(`/itempagedata?itemId=${itemId}`)
        .then((response) => response.json())
        .then((itemPageData) => {
          fetch("/login")
            .then((response) => response.json())
            .then((loginResponse) => {
              createSelectedItemCard(itemPageData.item);
              getItemPageComments(itemPageData.comments);
              if (!loginResponse.isUserLoggedIn) {
                hideCommentBox();
                const login = $("#login");
                login.append(
                  $(
                    '<a href="' +
                      loginResponse.LoginURL +
                      '"><button type="button" class="btn btn-dark my-sm-0">Login to Comment</button></a>'
                  )
                );
              }
            })
            .catch((error) => {
              console.log("failed to fetch login status: " + error);
            });
        });
    } else {
      console.log("ItemId is empty!");
    }
  });
}

/**
 * Hides input box when user is not logged in
 */
function hideCommentBox() {
  const comment = document.getElementById("comment-box");
  if (comment.style.display === "none") {
    comment.style.display = "block";
  } else {
    comment.style.display = "none";
  }
}

/**
 * Makes Entertainment Item into card
 */
async function createSelectedItemCard(entertainmentItem) {
  const card = $('<div class="mt-2" class="card bg-light"></div>');
  card.append(
    $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">')
  );
  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
    $(
      '<h5 class="card-title">' +
        entertainmentItem.title +
        "(" +
        entertainmentItem.releaseDate +
        ")" +
        "</h5>"
    )
  );

  cardBody.append(
    $(
      '<h5 class="card-title"><b>Runtime: </b>' +
        entertainmentItem.runtime +
        "</h5>"
    )
  );
  cardBody.append(
    $('<h5 class="card-title">' + entertainmentItem.genre + "</h5>")
  );
  cardBody.append(
    $(
      '<h5 class="card-title"><b>Cast: </b>' +
        entertainmentItem.actors +
        "</h5>"
    )
  );

  cardBody.append(
    $(
      '<p class="card-text"><b>Description: </b>' +
        entertainmentItem.description +
        "</p>"
    )
  );
  card.append(cardBody);
  const itemContainer = $("#item-container");
  itemContainer.append(card);
}

/**
 * Sends comment data and ItemId to Servlet
 */
async function sendFormData() {
  const comment = $("#comment").val();
  const itemId = getUrlParam("itemId");
  if (comment != "" && itemId != null) {
    $.post("/itempagedata", { comment: comment, itemId: itemId })
      .done(function () {
        window.location.reload();
      })
      .fail(function () {
        console.log("Failed to send form data");
      });
  }
}

/**
 * Function which builds the comment element from the ItemPageData object
 */
function getItemPageComments(comments) {
  const commentContainer = $("#comment-container");
  comments.forEach((commentDataManager) => {
    const date = new Date(commentDataManager.timestampMillis);
    const commentId = commentDataManager.commentId;
    commentContainer.append(
      createListElement(
        commentDataManager.username +
          ": " +
          commentDataManager.comment +
          " - " +
          "(" +
          date.toLocaleString() +
          ")",
        commentDataManager.belongsToUser,
        commentDataManager.email
      )
    );
  });
}

/**
 * Creates list element from given comment
 *
 * @param { string } comment - the comment including the username, message, and timestamp
 * @param { boolean } belongsToUser - boolean that shows whether or not the current user has posted a comment
 * @param { string } email - the email of the user
 * @returns { html element }  returns a list element
 */
function createListElement(comment, belongsToUser, email) {
  const liElement = $('<li class="list-group-item"></li>');
  liElement.text(comment);
  if (belongsToUser) {
    liElement.append($('<i class="fa fa-trash" style="float:right;"></i>'));
  }
  liElement.append(
    $(
      '<img class="pr-1" src="' +
        "https://icotar.com/avatar/" +
        email.charAt(0) +
        ".png?s=23" +
        '"style=float:left;"></img>'
    )
  );
  return liElement;
}

/**
 * Finds a query string parameter in the current Url.
 *
 * @param { string } param - the parameter to look for in the window Url
 * @returns { string } if the parameter exists it returns the value found,
 *     otherwise an empty string
 */
function getUrlParam(param) {
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.has(param)) {
    return urlParams.get(param);
  }

  return "";
}
