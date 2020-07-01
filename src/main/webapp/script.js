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
 * Fetches omdb item information from OMDb API.
 */
function getOmdbItem() {
  fetch('https://www.omdbapi.com/?apikey=a28d48cd&t=' + $('#itemTitle').val())
      .then((response) => response.json())
      .then((omdbItem) => {
        const omdbItemEntry = $('#omdbItemEntry');
        omdbItemEntry.empty();

        if (omdbItem.Response === 'False') {
          omdbItemEntry.append($('<p>Item not found!</p>'));
        } else {
          omdbItemEntry.append(createOmdbItemCard(omdbItem));
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
    // The grid gets added a new row if there are items that still haven't been
    // included.
    const rowElem = $('<div class="row mb-4"></div>');

    const MAX_CELLS_PER_ROW = 3;

    // The loop adds cells containing cards to the current row element until it
    // reaches the maximum limit of cells per row, or if all the items on the
    // list have been included.
    for (let cell = 0; cell < MAX_CELLS_PER_ROW &&
         currItemIndex < entertainmentItemsList.length;
         cell++, currItemIndex++) {
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
 * @param { JSON } entertainmentItem - the entertainment item whose
 *     data will be displayed in the card element
 * @returns { jQuery } card element representing the entertainment item
 */
function createEntertainmentItemCard(entertainmentItem) {
  const card = $('<div class="card bg-light"></div>');
  card.append(
      $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));
  card.append(
      $('<a class="stretched-link" href="item-page.html?itemId=' +
        entertainmentItem.uniqueId + '"></a>'));

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
 * about an omdb item.
 *
 * @param { JSON } omdbItem - item whose data will be displayed in the card
 *     element
 * @returns { jQuery } card element representing the omdbItem
 */
function createOmdbItemCard(omdbItem) {
  const card = $('<div class="card bg-light"></div>');
  card.append(
      $('<img class="card-img-top mx-auto item-image" src="' + omdbItem.Poster +
        '">'));

  const cardBody = $('<div class="card-body"></div>');
  cardBody.append($('<h5 class="card-title">' + omdbItem.Title + '</h5>'));
  cardBody.append(
      $('<p class="card-text">Released: ' + omdbItem.Released + '</p>'));

  card.append(cardBody);

  return card;
}

/**
 * Loads a selector from another HTML file so that it can be used in the current
 * DOM.
 *
 * @param { string } selector - Selector that will be used across the document
 *     to refer to the HTML element
 * @param { string } filename - Filename of HTML file that is used to load the
 *     element
 *
 * @example loadSelector("#navbar", "navbar.html")
 */
function loadSelector(selector, filename) {
  $(document).ready(function() {
    $(selector).load(filename);
  });
}
