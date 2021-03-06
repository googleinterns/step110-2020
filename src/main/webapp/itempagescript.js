/**
 * Retrieves ItemPageData and forms page using other functions.
 */
function loadItemPage() {
  $(document).ready(function() {
    $('#navbar').load('navbar.html', function() {
      initializeNavBarProfileSection();
    });

    const itemId = getUrlParam('itemId');

    if (itemId !== '') {
      fetch(`/itempagedata?itemId=${itemId}`)
          .then((response) => response.json())
          .then((itemPageData) => {
            fetch('/login')
                .then((response) => response.json())
                .then((loginResponse) => {
                  createSelectedItemCard(itemPageData.item);
                  getItemPageComments(itemPageData.comments);

                  if (!loginResponse.isUserLoggedIn) {
                    hideCommentBox();

                    const login = $('#login');
                    login.append($(
                        '<a href="' + loginResponse.LoginURL +
                        '"><button type="button" class="btn btn-dark my-sm-0">Login to Comment</button></a>'));
                  }
                })
                .catch((error) => {
                  console.log('failed to fetch login status: ' + error);
                });
          });
    } else {
      console.log('ItemId is empty!');
    }
  });
}

/**
 * Hides input box when user is not logged in
 */
function hideCommentBox() {
  const comment = document.getElementById('comment-box');
  if (comment.style.display === 'none') {
    comment.style.display = 'block';
  } else {
    comment.style.display = 'none';
  }
}

/**
 * Makes Entertainment Item into card
 *
 * @param { JSON } entertainmentItem - the item whose data will be displayed in
 *     the card
 */
async function createSelectedItemCard(entertainmentItem) {
  const card = $('<div class="mt-2 card bg-light border-dark"></div>');
  card.append(
      $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));

  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
      $('<h5 class="card-title text-center">' + entertainmentItem.title +
        '</h5>'));
  cardBody.append(
      $('<p class="card-text"><b>Description: </b>' +
        entertainmentItem.description + '</p>'));
  cardBody.append(
      $('<p class="card-text"><b>Release Date: </b>' +
        entertainmentItem.releaseDate + '</p>'));
  cardBody.append(
      $('<p class="card-text"><b>Runtime: </b>' + entertainmentItem.runtime +
        '</p>'));
  cardBody.append(
      $('<p class="card-text"><b>Genre: </b>' + entertainmentItem.genre +
        '</p>'));
  cardBody.append(
      $('<p class="card-text"><b>Directors: </b>' +
        entertainmentItem.directors + '</p>'));
  cardBody.append(
      $('<p class="card-text"><b>Writers: </b>' + entertainmentItem.writers +
        '</p>'));
  cardBody.append(
      $('<p class="card-text"><b>Actors: </b>' + entertainmentItem.actors +
        '</p>'));

  card.append(cardBody);

  const itemContainer = $('#item-container');
  itemContainer.append(card);
}

/**
 * Submits a new comment to the ItemPageServlet
 */
function submitComment() {
  const comment = $('#comment').val();
  const itemId = getUrlParam('itemId');

  if (comment != '' && itemId != null) {
    $.post('/itempagedata', {comment: comment, itemId: itemId})
        .done(function() {
          window.location.reload();
        })
        .fail(function() {
          console.log('Failed to send form data');
        });
  }
}

/**
 * Function which builds the comment element from the ItemPageData object
 * @param { JSON } comments - list of comments to display
 */
function getItemPageComments(comments) {
  const commentContainer = $('#comment-container');
  comments.forEach((comment) => {
    commentContainer.append(createCommentElement(comment));
  });
}

/**
 * Sends delete request to servlet for a specific comment.
 *
 * @param { jQuery } commentElem - the element displaying the comment data
 * @param { string } commentId - the commentId for the given comment
 */
function deleteComment(commentElem, commentId) {
  fetch('/itempagedata?commentId=' + commentId, {method: 'DELETE'})
      .then(() => {
        commentElem.remove();
      })
      .catch((error) => {
        console.log('Failed to delete comment: ' + error);
      });
}

/**
 * Creates list element displaying comment information
 *
 * @param { JSON } comment - the comment whose data is displayed on the list
 *     element
 * @returns { jQuery }  returns a list element
 */
function createCommentElement(comment) {
  const commentElem = $('<li class="list-group-item border"></li>');

  const commentHeader = $('<div class="row no-gutters my-1"></div>');
  commentElem.append(commentHeader);

  const userUrl = '/user-profile-page.html?username=' + comment.username;

  commentHeader.append(createUserAvatar(comment.email, userUrl));
  commentHeader.append(createUsernameText(comment.username, userUrl));
  commentHeader.append(createCommentDate(comment.timestampMillis));

  commentElem.append($('<p>' + comment.comment + '</p>'));

  if (comment.belongsToUser) {
    commentElem.append(
        createDeleteCommentButton(commentElem, comment.commentId));
  }

  return commentElem;
}

/**
 * Creates image div that displays avatar used in comment.
 *
 * @param { string } email - the email used to create the avatar for the comment
 * @param { string } userUrl - the url that links to the user's profile
 * @returns { jQuery } image div displaying avatar
 */
function createUserAvatar(email, userUrl) {
  const userAvatar =
      $('<img class="col-md-auto mr-2" src="' + getProfileImageUrl(email) +
        '" width="24" height="24">');
  userAvatar.click(function() {
    window.location.href = userUrl;
  });

  return userAvatar;
}

/**
 * Creates text div that displays the username that posted a comment
 *
 * @param { string } username - the username that posted the comment
 * @param { string } userUrl - the url that links to the user's profile
 * @returns { jQuery } text div displaying the username that posted a comment
 */
function createUsernameText(username, userUrl) {
  const usernameText = $('<p class="col-md-auto mr-2">' + username + '</p>');
  usernameText.append(
      $('<a class="stretched-link" href="' + userUrl + '"></a>'));

  return usernameText;
}

/**
 * Creates text div to display the date of the comment.
 *
 * @param { number } timestampMillis - the timestamp when the comment was posted
 * @returns { jQuery } text div displaying comment date
 */
function createCommentDate(timestampMillis) {
  const date = new Date(timestampMillis);
  const dateText =
      $('<p class="col-md-auto">(' + date.toLocaleString() + ')</p>');

  return dateText;
}

/**
 * Creates the button used to delete a comment by the user who owns it.
 * @param { jQuery } commentElem - div that contains comment
 * @param { number } commentId - unique Id used to identify the comment
 * @returns { jQuery } button used to delete comment
 */
function createDeleteCommentButton(commentElem, commentId) {
  const deleteButton =
      $('<div><i class="fa fa-trash" style="float:right;"></i></div>');
  deleteButton.one('click', () => {
    deleteComment(commentElem, commentId);
  });

  return deleteButton;
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

  return '';
}
