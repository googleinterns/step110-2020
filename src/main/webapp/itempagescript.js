/**
 * Retrieves ItemPageData and forms page using other functions.
 */
async function loadItemPage() {
  $(document).ready(function() {
    $('#navbar').load('navbar.html');
    
    const itemId = getUrlParam('itemId');

    if (itemId !== '') {
      fetch(`/itempagedata?itemId=${itemId}`)
        .then((response) => response.json())
        .then((ItemPageData) => {
          createSelectedItemCard(ItemPageData.item);
          getItemPageComments(ItemPageData.comments);
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
  const card = $('<div class="card bg-light"></div>');
  card.append(
    $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));
  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
    $('<h5 class="card-title">' + entertainmentItem.title + '</h5>'));
  cardBody.append(
    $('<p class="card-text">' + entertainmentItem.description + '</p>'));

  card.append(cardBody);
  const itemContainer = $('#item-container');
  itemContainer.append(card);
}

/**
 * Sends comment data and ItemId to Servlet
 */
async function sendFormData() {
  const itemId = getUrlParam('itemId');
  const comment = document.getElementById('comment');
  fetch(
    `/itempagedata?=${itemId}`,
    { method: 'post', body: JSON.stringify(comment) })
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

