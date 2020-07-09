/**
 * Fetches entertainment items from DashboardServlet
 * to populate the Dashboard.
 */
function getDashboardItems() {
  //TODO: Separate load function
  $(document).ready(function() {
    $('#navbar').load('navbar.html', function() {
        fetch(
      '/dashboard?cursor=' + getUrlParam('cursor') +
      '&searchValue=' + $('#searchValue').val() +
      '&sortingDirection=' + $('#sortingDirection').val())
      .then((response) => response.json())
      .then((entertainmentItemList) => {
        const entertainmentItemsContainer = $('#entertainmentItemsContainer');
        entertainmentItemsContainer.empty();

        const items = entertainmentItemList.items;

        populateItemGrid(entertainmentItemsContainer, items);
        updatePagination(items.length, entertainmentItemList.pageCursor);
      })
      .catch((error) => {
        console.log(
            'Failed to fetch entertainment items from DashboardServlet: ' +
            error);
      });
    });

    $('#itemSubmissionDiv').load('item-submission-dialog.html');
  });
}

/**
 * Fetches omdb item information from OMDb API and displays the item that was
 * found.
 */
function getOmdbItem() {
  fetch('https://www.omdbapi.com/?apikey=a28d48cd&t=' + $('#itemTitle').val())
      .then((response) => response.json())
      .then((omdbItem) => {
        const omdbItemEntry = $('#omdbItemEntry');
        omdbItemEntry.empty();

        const submitButton = $('#submitButton');

        if (omdbItem.Response === 'False') {
          omdbItemEntry.append($('<p>Item not found!</p>'));

          submitButton.addClass('d-none');
        } else {
          const itemCard = createOmdbItemCard(omdbItem);

          omdbItemEntry.append(itemCard);

          enableItemSubmissionIfUnique(submitButton, itemCard, omdbItem);
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
      const item = entertainmentItemsList[currItemIndex];

      // If uniqueId Optional is empty then the item should not be created.
      if ($.isEmptyObject(item.uniqueId) ||
          !item.uniqueId.hasOwnProperty('value')) {
        continue;
      }

      const colElem = $('<div class="col-md-4"</div>');
      colElem.append(createEntertainmentItemCard(item));

      rowElem.append(colElem);
    }

    entertainmentItemsContainer.append(rowElem);
  }
}

/**
 * Creates a card element that displays poster image, title, and description
 * about a specific entertainment item. The item should have a valid uniqueId;
 * otherwise, the Url associated with the card will have an invalid query string
 * parameter.
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
        entertainmentItem.uniqueId.value + '"></a>'))

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
 * Makes the item submission button visible if the item is not a duplicate.
 *
 * @param { jQuery } submitButton - button used to submit omdb items
 * @param { jQuery } itemCard - card div that displays info about the item found
 * @param { JSON } omdbItem - the item found by omdb API
 */
function enableItemSubmissionIfUnique(submitButton, itemCard, omdbItem) {
  fetch('/item-submission?imdbID=' + omdbItem.imdbID)
      .then((response) => response.json())
      .then((isItemUnique) => {
        if (isItemUnique) {
          submitButton.removeClass('d-none');
          submitButton.off().one('click', () => {
            submitItem(omdbItem);
          });
        } else {
          // TODO: Add link to item that already exists.
          itemCard.append($(
              '<p class="card-text">Item already exists on Entertainment Hub!</p>'));
        }
      })
      .catch((error) => {
        console.log('failed to check if omdb Item is duplicate: ' + error);
      });
}

/**
 * Sends an omdbItem to the ItemSubmissionServlet using a Post request.
 *
 * @param { JSON } omdbItem - item that will be sent to the
 *     ItemSubmissionServlet through a Post request
 */
function submitItem(omdbItem) {
  $.post('/item-submission', omdbItem)
      .done(function() {
        // If the item gets added, the page needs to reload to see the
        // changes.
        window.location.reload();
      })
      .fail(function() {
        console.log('Failed to submit entertainment item!');
      });
}

/**
 * Loads the stored values for the sorting selector and populates dashboard with
 * the Entertainment Items.
 */
function loadDashboard() {
  $('#sortingDirection').change(function() {
    localStorage.setItem('sortDir', $(this).val());
    getDashboardItems();
  });

  $(document).ready(function() {
    const sortDir = localStorage.getItem('sortDir');

    if (sortDir !== null) {
      $(`#sortingDirection`).val(sortDir);
    }

    getDashboardItems();
  });
}

/**
 * Updates the display of buttons used for pagination if the
 * current page is full.
 *
 * @param { number } itemsInPage - number of items loaded in the page
 * @param { string } pageCursor - opaque key representing the cursor for the
 *     next page
 */
function updatePagination(itemsInPage, pageCursor) {
  const MAX_ITEMS_PER_PAGE = 18;
  // Display pagination button only when needed(page is full).
  if (itemsInPage === MAX_ITEMS_PER_PAGE) {
    $('#paginationNav').removeClass('d-none');
    $('#nextLink').attr('href', '/index.html?cursor=' + pageCursor);
  } else {
    $('#paginationNav').addClass('d-none');
  }
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
