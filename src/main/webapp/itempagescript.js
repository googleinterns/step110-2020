/**
 * Retrieves ItemPageData and forms page using other functions.
 */
async function loadItemPage() {
  const itemId = getItemId();
  console.log(itemId);
  if (itemId != null) {
    fetch(`/itempagedata?itemId=${itemId}`).then((response) => response.json()).then((ItemPageData) => {
      createSelectedItemCard(ItemPageData.item);
      getItemPageComments(ItemPageData.comments);
    });
  } else {
    console.log('ItemId is null');
  }
}

/**
 * Makes Entertainment Item into card
 */
async function createSelectedItemCard(entertainmentItem) {
  const card = $('<div class="card bg-light"></div>');
  card.append(
    $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));
  const cardBody = $('<div class="card-body"></div>');
  cardBody.append( $('<p class="card-text">' + entertainmentItem.description + '</p>'));
  card.append(cardBody);
  const itemContainer = $('#item-container');
  itemContainer.append(card);
}

/**
 * Gets itemId from current URL
 */
function getItemId() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  return urlParams.get('itemId');
}

/**
 * Sends comment data and ItemId to Servlet
 */
async function sendFormData() {
  const comment = $('#comment').val();
  const itemId = getItemId();
  $.post('/itempagedata', { comment: comment, itemId: itemId }).done(function() {
    window.location.reload();
  }).fail(function() {
    console.log('Failed to send form data');
  });
}

/**
 * Function which builds the comment element from the ItemPageData object
 */
function getItemPageComments(comments) {
  const commentContainer = document.getElementById('comment-container');
  comments.forEach((commentDataManager) => {
    const date = new Date(commentDataManager.timestampMillis);
    commentContainer.appendChild(createListElement(
      commentDataManager.message + ' - ' +
      '(' + date.toUTCString() + ')'));
  });
}

/**
 * Creates list element which houses comments
 */
function createListElement(comment) {
  const liElement = document.createElement('li');
  liElement.innerText = comment;
  return liElement;
}

