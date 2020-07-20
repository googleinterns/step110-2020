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
            createSelectedItemCard(itemPageData.item);
            getItemPageComments(itemPageData.comments);
          });
    } else {
      console.log('ItemId is empty!');
    }
  });
}

/**
 * Makes Entertainment Item into card
 */
async function createSelectedItemCard(entertainmentItem) {
  const card = $('<div class="mt-2" class="card bg-light"></div>');
  card.append(
      $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));
  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
      $('<h5 class="card-title">' + entertainmentItem.title + '(' +
        entertainmentItem.releaseDate + ')' +
        '</h5>'));
  cardBody.append(
      $('<h5 class="card-title">' + entertainmentItem.genre + '</h5>'));
  cardBody.append(
      $('<p class="card-text"><b>Description: </b>' +
        entertainmentItem.description + '</p>'));

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
 */
function getItemPageComments(comments) {
  const commentContainer = $('#comment-container');
  comments.forEach((commentDataManager) => {
    const date = new Date(commentDataManager.timestampMillis);
    commentContainer.append(createListElement(
        commentDataManager.comment + ' - ' +
        '(' + date.toLocaleString() + ')'));
  });
}

/**
 * Creates list element which houses comments.
 */
function createListElement(comment) {
  const liElement = document.createElement('li');
  liElement.className = 'list-group-item';
  liElement.append(comment);
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
