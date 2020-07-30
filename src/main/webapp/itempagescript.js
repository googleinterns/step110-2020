/**
 * Retrieves ItemPageData and forms page using other functions.
 */
async function loadItemPage() {
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
function createSelectedItemCard(entertainmentItem) {
  const card = $('<div class="mt-2 card bg-light"></div>');
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
 * Sends comment data and ItemId to Servlet
 */
async function sendFormData() {
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
    commentContainer.append(createListElement(comment));
  });
}

/**
 * Sends delete request to servlet for a specific comment.
 *
 * @param { string } commentId - the commentId for the given comment
 */
function deleteComment(commentId) {
  fetch('/itempagedata?commentId=' + commentId, {method: 'DELETE'})
      .then(() => {
        location.reload();
      })
      .catch((error) => {
        console.log('Failed to delete comment');
      });
}

/**
 * Creates list element from given comment
 *
 * @param { JSON } comment - the comment whose data is displayed on the list
 *     element
 * @returns { jQuery }  returns a list element
 */
function createListElement(comment) {
  const liElement = $('<li class="list-group-item"></li>');
  const date = new Date(comment.timestampMillis);

  liElement.text(
      comment.username + ': ' + comment.comment + ' - ' +
      '(' + date.toLocaleString() + ')');

  liElement.append(
      $('<img class="pr-1" src="' + getProfileImageUrl(comment.email) +
        '.png?s=23' + '"style=float:left;"></img>'));


  if (comment.belongsToUser) {
    liElement.append(
        $('<div onclick="deleteComment(' + comment.commentId +
          ')"><i class="fa fa-trash" style="float:right;"></i></div>'));
  }

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

  return '';
}
