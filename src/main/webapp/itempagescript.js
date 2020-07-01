/**
 * Retrieves ItemPageData and forms page using other functions.
 */
async function loadItemPage() {
  const itemId = getItemId();
  console.log(itemId);
  if (itemId != null) {
    fetch(`/itempagedata?itemId=${itemId}`)
        .then((response) => response.json())
        .then((ItemPageData) => {
          createSelectedItemCard(ItemPageData.item);
          getItemPageComments(ItemPageData.comments);
        })
  } else {
    console.log('ItemId is null');
  }
}


function createSelectedItemCard(entertainmentItem) {
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
async function getItem() {
  const itemId = getItemId();
  console.log(itemId);
  const response = await fetch(`/itempagedata?itemId=${itemId}`);
  const selectedItem = await response.json();
  createSelectedItemCard(selectedItem);
}

/**
 * Gets itemId from current URL
 */
function getItemId() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const itemId = urlParams.get('itemId');
  return itemId;
}

/**
 * Sends comment data and ItemId to Servlet
 */
function sendFormData() {
  const itemId = getItemId();
  const comment = document.getElementById('comment');
  fetch(
      `/itempagedata?=${itemId}`,
      {method: 'post', body: JSON.stringify(comment)});
}

/**
 * Function which builds the comment element from the ItemPageData object
 */
function getItemPageComments(comments) {
  const commentContainer = document.getElementById('comment-container');
  comments.forEach((commentObject) => {
    const date = new Date(commentObject.timestampMillis);
    commentContainer.appendChild(createListElement(
        commentObject.message + ' - ' +
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
