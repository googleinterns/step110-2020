/**
 * Loads the dashboard nav-bar, the item submission dialog, and the grid of
 * entertainment items.
 */
function loadDashboard() {
  $(document).ready(function() {
    $('#navbar').load('navbar.html', function() {
      $('#navbarDashboardSection').removeClass('d-none');

      setupNavBarProfileSection();
      getDashboardItems();
    });

    $('#itemSubmissionDiv').load('item-submission-dialog.html');
  });
}

/**
 * Fetches entertainment items from DashboardServlet
 * to populate the Dashboard.
 *
 * @param { boolean } clearCurrentItems - clears and loads the entertainment
 *     items again if true
 * @param { string } cursor - the cursor pointing to the page location
 * to get the items from
 */
function getDashboardItems(clearCurrentItems = true, cursor = '') {
  fetch(
      '/dashboard?cursor=' + cursor +
      '&searchValue=' + $('#searchValue').val() +
      '&sortType=' + $('#sortType').val())
      .then((response) => response.json())
      .then((entertainmentItemList) => {
        const itemContainer = $('#entertainmentItemsContainer');

        // Pagination does not need to refresh dashboard items, but searching
        // and sorting does.
        if (clearCurrentItems) {
          itemContainer.empty();
        }

        populateItemGrid(itemContainer, entertainmentItemList.items);
        updatePagination(entertainmentItemList.pageCursor);
      })
      .catch((error) => {
        console.log(
            'Failed to fetch entertainment items from DashboardServlet: ' +
            error);
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
    const rowElem = $('<div class="row mb-3"></div>');

    const MAX_CELLS_PER_ROW = 3;

    // The loop adds cells containing cards to the current row element until it
    // reaches the maximum limit of cells per row, or if all the items on the
    // list have been included.
    for (let cell = 0; cell < MAX_CELLS_PER_ROW &&
         currItemIndex < entertainmentItemsList.length;
         cell++, currItemIndex++) {
      const item = entertainmentItemsList[currItemIndex];

      // If uniqueId Optional is empty then the item should not be created.
      if (isOptionalEmpty(item.uniqueId)) {
        continue;
      }

      const colElem = $('<div class="col-md-4 mb-3"</div>');
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
      .then((itemFound) => {
        if (/* item is unique */ isOptionalEmpty(itemFound)) {
          submitButton.removeClass('d-none');
          submitButton.off().one('click', () => {
            submitItem(omdbItem);
          });
        } else {
          const itemId = itemFound.value.uniqueId;
          let itemLink = '';

          if (!isOptionalEmpty(itemId)) {
            itemLink = ' <a href="item-page.html?itemId=' + itemId.value +
                '">Link to Item</a>';
          }

          itemCard.append($(
              '<p class="card-text">Item already exists on Entertainment Hub!' +
              itemLink + '</p>'));
        }
      })
      .catch((error) => {
        console.log('failed to check if omdb Item is duplicate: ' + error);
      });
}

/**
 * Enables access to the profile if the user is logged in by adding a "Profile" link to the navbar,
 * if the user is not logged in then it adds a link to login.
 */
function setupNavBarProfileSection() {
  fetch('/login')
      .then((response) => response.json())
      .then((isUserLoggedIn) => {
        const profileLinks = $('#profileLinks');

        if (isUserLoggedIn) {
          profileLinks.append($(
              '<a class="nav-link text-light" href="/ProfilePage.html">Profile</a>'));
        } else {
          profileLinks.append($(
              '<a class="nav-link text-light" href="/LoginPage.html">Login</a>'));
        }
      })
      .catch((error) => {
        console.log('failed to fetch login status: ' + error);
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
 * Fetches for more entertainment items if the current page is fully scrolled
 * down.
 *
 * @param { string } pageCursor - opaque key representing the cursor for the
 *     next page
 */
function updatePagination(pageCursor) {
  $(window).off().scroll(function() {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
      $(window).off('scroll');

      getDashboardItems(/* clearItems */ false, pageCursor);
    }
  });
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

/**
 * Checks if an object contains an optional value.
 *
 * @param { Optional } optional - the object containing an optional value
 * @returns { boolean } if the optional is empty it returns true, otherwise
 *     false
 */
function isOptionalEmpty(optional) {
  return $.isEmptyObject(optional) || !optional.hasOwnProperty('value');
}
