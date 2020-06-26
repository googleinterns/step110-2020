/**
 * Fetches entertainment items from DashboardServlet
 * to populate the Dashboard.
 */
function getDashboardItems() {
  fetch(
      '/dashboard?searchValue=' + $('#searchValue').val() +
      '&sortingDirection=' + $('#sortingDirection').val())
      .then((response) => response.json())
      .then((entertainmentItemsList) => {
        const entertainmentItemsContainer = $('#entertainmentItemsContainer');
        entertainmentItemsContainer.empty();

        populateItemGrid(entertainmentItemsContainer, entertainmentItemsList);
      })
      .catch((error) => {
        console.log(
            'Failed to fetch entertainment items from DashboardServlet: ' +
            error);
      });
}

/**
 * Fetches OMDb item information from OMDb API.
 */
function getOMDbItem() {
  fetch('https://www.omdbapi.com/?apikey=a28d48cd&t=' + $('#itemTitle').val())
      .then((response) => response.json())
      .then((OMDbItem) => {
        const OMDbItemEntry = $('#OMDbItemEntry');
        OMDbItemEntry.empty();

        if (OMDbItem.Response === 'False') {
          OMDbItemEntry.append($('<p>Item not found!</p>'))
        } else {
          OMDbItemEntry.append(createOMDbItemCard(OMDbItem));
        }
      })
      .catch((error) => {
        console.log('Failed to fetch movie from OMDb API: ' + error);
      });
}

/**
 * Populates the item grid used in the Dashboard with all the entertainment
 * items on the list.
 *
 * @param { jQuery } entertainmentItemsContainer - the div element used as a
 *     parent in which to add the rows and columns of the grid
 * @param { Array } entertainmentItemsList - the list of EntertainmentItems to
 *     add to the grid
 */
function populateItemGrid(entertainmentItemsContainer, entertainmentItemsList) {
  let currItemIndex = 0;

  while (currItemIndex < entertainmentItemsList.length) {
    const rowElem = $('<div class="row mb-4"></div>');

    const MAX_COLS_PER_ROW = 3;

    for (let col = 1; col <= MAX_COLS_PER_ROW &&
         currItemIndex < entertainmentItemsList.length;
         col++, currItemIndex++) {
      const colElem = $('<div class="col-md-4"</div>');
      colElem.append(
          createEntertainmentItemCard(entertainmentItemsList[currItemIndex]));

      rowElem.append(colElem);
    }

    entertainmentItemsContainer.append(rowElem);
  }
}

/**
 * Creates a card element that displays poster image, title, and description
 * about a specific entertainment item.
 *
 * @param { JSON } entertainmentItem the entertainment item whose
 *     data will be displayed in the card element
 * @returns { jQuery } card element representing the entertainment item
 */
function createEntertainmentItemCard(entertainmentItem) {
  const card = $('<div class="card bg-light"></div>');
  card.append(
      $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));
  card.append(
      $('<a class="stretched-link" href="item-page.html?itemId=' +
        entertainmentItem.uniqueId + '"></a>'))

  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
      $('<h5 class="card-title">' + entertainmentItem.title + '</h5>'));
  cardBody.append(
      $('<p class="card-text">' + entertainmentItem.description + '</p>'));

  card.append(cardBody);

  return card;
}

/**
 * Creates a card element that displays poster image, title, and release date
 * about an OMDbItem.
 *
 * @param { JSON } the OMDbItem whose data will be displayed in the card element
 * @returns { jQuery } card element representing the OMDbItem
 */
function createOMDbItemCard(OMDbItem) {
  const card = $('<div class="card bg-light"></div>');
  card.append(
      $('<img class="card-img-top mx-auto item-image" src="' + OMDbItem.Poster +
        '">'));

  const cardBody = $('<div class="card-body"></div>');
  cardBody.append($('<h5 class="card-title">' + OMDbItem.Title + '</h5>'));
  cardBody.append(
      $('<p class="card-text">Released: ' + OMDbItem.Released + '</p>'));

  card.append(cardBody);

  return card;
}
