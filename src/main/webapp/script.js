/**
 * Loads the dashboard nav-bar, item submission dialog, search input, sort
 * selector, and the grid of entertainment items to completely setup the
 * dashboard page.
 */
function loadDashboardPage() {
  $(document).ready(function() {
    $('#itemSubmissionDiv').load('item-submission-dialog.html');

    $('#navbar').load('navbar.html', function() {
      $('#navbarDashboardSection').removeClass('d-none');

      initializeNavBarProfileSection();
      initializeSearchInput();
      initializeSortSelector();
      initializeDashboard();
    });
  });
}

/**
 * Enables access to the profile if the user is logged in by adding a "Profile"
 * link to the navbar, if the user is not logged in then it adds a link to
 * login.
 */
function initializeNavBarProfileSection() {
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
 * Loads the stored value used for the search input from sessionStorage
 */
function initializeSearchInput() {
  const searchVal = sessionStorage.getItem('searchVal');

  if (searchVal !== null) {
    $('#searchValue').val(searchVal);
  }
}

/**
 * Loads the stored values for the sort type of the sort selector from
 * sessionStorage
 */
function initializeSortSelector() {
  const sortVal = sessionStorage.getItem('sortVal');

  if (sortVal !== null) {
    $('#sortType').val(sortVal);
  }
}

/**
 * Fetches for the entertainment items from DashboardServlet and the favorite
 * item ids from FavoriteItemServlet to initialize the dashboard with items that
 * have correct like button state.
 */
async function initializeDashboard() {
  Promise
      .allSettled([
        fetch(
            '/dashboard?searchValue=' + $('#searchValue').val() +
            '&sortType=' + $('#sortType').val()),
        fetch('/favorite-item')
      ])
      .then(async (results) => {
        dashboardResult = results[0];
        favResult = results[1];

        if (!dashboardResult.value.ok) {
          console.log(
              'Failed to fetch Entertainment Items from DashboardServlet: ' +
              dashboardResult.reason);
          return;
        }

        try {
          const entertainmentItems = await dashboardResult.value.json();
          const favoriteItemIds =
              favResult.value.ok ? await favResult.value.json() : [];

          setupSeachInputCallback(favoriteItemIds);
          setupSortSelectorCallback(favoriteItemIds);
          updateDashboardItems(entertainmentItems, favoriteItemIds);
        } catch (error) {
          console.log('Failed to populate dashboard: ' + error);
        }
      });
}

/**
 * Adds an input value callback to the search input for it to update its value
 * to sessionStorage and fetch for new items.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 */
function setupSeachInputCallback(favoriteItemIds) {
  $('#searchValue').on('input', function() {
    sessionStorage.setItem('searchVal', $(this).val());
    getEntertainmentItems(favoriteItemIds);
  });
}

/**
 * Adds a change value callback to the sort selector for it to update its value
 * to sessionStorage and fetch for new items.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 */
function setupSortSelectorCallback(favoriteItemIds) {
  $('#sortType').change(function() {
    sessionStorage.setItem('sortVal', $(this).val());
    getEntertainmentItems(favoriteItemIds);
  });
}

/**
 * Updates the dashboard with the new items that are given.
 *
 * @param { JSON } entertainmentItems - the entertainment items JSON object
 *     obtained by fetching the DashboardServlet
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { boolean } clearCurrentItems - clears and loads the entertainment
 *     items again if true
 */
function updateDashboardItems(
    entertainmentItems, favoriteItemIds, clearCurrentItems = true) {
  const itemContainer = $('#entertainmentItemsContainer');

  // Pagination does not need to refresh dashboard items, but searching
  // and sorting does.
  if (clearCurrentItems) {
    itemContainer.empty();
  }

  populateItemGrid(itemContainer, entertainmentItems.items, favoriteItemIds);
  updatePagination(favoriteItemIds, entertainmentItems.pageCursor);
}

/**
 * Fetches for entertainment items from the DashboardServlet and then updates
 * the dashboard grid with the items that were retrieved.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { string } pageCursor - opaque key representing the cursor for the
 *     next page
 * @param { boolean } clearCurrentItems - clears and loads the entertainment
 *     items again if true
 */
function getEntertainmentItems(
    favoriteItemIds, pageCursor = '', clearCurrentItems = true) {
  fetch(
      '/dashboard?cursor=' + pageCursor + '&searchValue=' +
      $('#searchValue').val() + '&sortType=' + $('#sortType').val())
      .then((response) => response.json())
      .then((entertainmentItems) => {
        updateDashboardItems(
            entertainmentItems, favoriteItemIds, clearCurrentItems);
      })
      .catch((error) => {
        console.log(
            'Failed to fetch Entertainment Items on page with cursor: ' +
            pageCursor + ', and error: ' + error);
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

          updateItemSubmission(submitButton, itemCard, omdbItem);
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
 * @param { Array } entertainmentItems - the list of entertainment items to
 *     add to the grid
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 */
function populateItemGrid(
    entertainmentItemsContainer, entertainmentItems, favoriteItemIds) {
  let currItemIndex = 0;

  while (currItemIndex < entertainmentItems.length) {
    // The grid gets added a new row if there are items that still haven't been
    // included.
    const rowElem = $('<div class="row mb-3"></div>');

    const MAX_CELLS_PER_ROW = 3;

    // The loop adds cells containing cards to the current row element until it
    // reaches the maximum limit of cells per row, or if all the items on the
    // list have been included.
    for (let cell = 0;
         cell < MAX_CELLS_PER_ROW && currItemIndex < entertainmentItems.length;
         cell++, currItemIndex++) {
      const item = entertainmentItems[currItemIndex];

      // If uniqueId Optional is empty then the item should not be created.
      if (isOptionalEmpty(item.uniqueId)) {
        continue;
      }

      const colElem = $('<div class="col-md-4 mb-3"</div>');
      colElem.append(createEntertainmentItemCard(item, favoriteItemIds));

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
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @returns { jQuery } card element representing the entertainment item
 */
function createEntertainmentItemCard(entertainmentItem, favoriteItemIds) {
  const card = $('<div class="card bg-light"></div>');
  card.append(
      $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));
  card.append(
      $('<a class="stretched-link" href="item-page.html?itemId=' +
        entertainmentItem.uniqueId.value + '"></a>'))

  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
      $('<h5 class="card-title text-center">' + entertainmentItem.title +
        '</h5>'));
  cardBody.append(
      $('<p class="card-text text-center">' + entertainmentItem.description +
        '</p>'));
  cardBody.append(
      createLikeButton(favoriteItemIds, entertainmentItem.uniqueId.value));

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
  cardBody.append(
      $('<h5 class="card-title text-center">' + omdbItem.Title + '</h5>'));
  cardBody.append(
      $('<p class="card-text text-center">Released: ' + omdbItem.Released +
        '</p>'));

  card.append(cardBody);

  return card;
}

/**
 * Creates the like button for a specific entertainment item and adds all its
 * necessary callbacks.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { number } itemId - the Id of the item that is going to be connected
 *     to the like button
 * @returns { jQuery } a new button ready to be displayed in the entertainment
 *     item card
 */
function createLikeButton(favoriteItemIds, itemId) {
  const likeButton = $('<button class="btn">Like</button>');

  const likeCounter = $('<span class="badge badge-light ml-2"></span>');
  likeButton.append(likeCounter);

  if (favoriteItemIds.includes(itemId)) {
    switchToUndoLikeButton(favoriteItemIds, itemId, likeButton, likeCounter);
  } else {
    switchToLikeButton(favoriteItemIds, itemId, likeButton, likeCounter);
  }

  updateLikeCounter(itemId, likeCounter);

  return likeButton;
}

/**
 * Fetches FavoriteItemServlet to add a like to a specific entertainment item.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { number } itemId - the Id used to identify the entertainment item
 * @param { jQuery } likeButton - the button that adds a like to the
 *     entertainment item
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function addLikeToEntertainmentItem(
    favoriteItemIds, itemId, likeButton, likeCounter) {
  $.post('/favorite-item?favoriteItemId=' + itemId)
      .done(function() {
        switchToUndoLikeButton(
            favoriteItemIds, itemId, likeButton, likeCounter);
        updateLikeCounter(itemId, likeCounter);

        // Add item to favorite list to avoid fetching for the list again.
        favoriteItemIds.push(itemId);
      })
      .fail(function() {
        console.log(
            'Failed to add a like to the entertainment item with Id: ' +
            itemId);
      });
}

/**
 * Fetches FavoriteItemServlet to remove a like from a specific entertainment
 * item.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { number } itemId - the Id used to identify the entertainment item
 * @param { jQuery } likeButton - the button that adds a like to the
 *     entertainment item
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function removeLikeFromEntertainmentItem(
    favoriteItemIds, itemId, likeButton, likeCounter) {
  fetch('/favorite-item?favoriteItemId=' + itemId, {method: 'DELETE'})
      .then(() => {
        switchToLikeButton(favoriteItemIds, itemId, likeButton, likeCounter);
        updateLikeCounter(itemId, likeCounter);

        // Remove item from favorite list to avoid fetching for the list again.
        favoriteItemIds.splice(favoriteItemIds.indexOf(itemId, 1));
      })
      .catch((error) => {
        console.log(
            'Failed to remove like from the entertainment item with Id: ' +
            itemId);
      });
}

/**
 * Toggles the like button to use like item functionality.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { number } itemId - the Id used to identify the entertainment item
 * @param { jQuery } likeButton - the button that adds a like to the
 *     entertainment item
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function switchToLikeButton(favoriteItemIds, itemId, likeButton, likeCounter) {
  likeButton.addClass('btn-secondary');
  likeButton.removeClass('btn-dark');
  likeButton.off().click(function() {
    addLikeToEntertainmentItem(
        favoriteItemIds, itemId, likeButton, likeCounter);
  });
}

/**
 * Toggles the like button to use undo-like item functionality.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { number } itemId - the Id used to identify the entertainment item
 * @param { jQuery } likeButton - the button that adds a like to the
 *     entertainment item
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function switchToUndoLikeButton(
    favoriteItemIds, itemId, likeButton, likeCounter) {
  likeButton.addClass('btn-dark');
  likeButton.removeClass('btn-secondary');
  likeButton.off().click(function() {
    removeLikeFromEntertainmentItem(
        favoriteItemIds, itemId, likeButton, likeCounter);
  });
}

/**
 * Fetches for the amount of likes an entertainment item has and updates its
 * counter with that value.
 *
 * @param { number } itemId - the Id of the item that is going to be connected
 *     to the like counter
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function updateLikeCounter(itemId, likeCounter) {
  fetch('/favorite-counter?itemId=' + itemId)
      .then((response) => response.json())
      .then((numberOfLikes) => {
        likeCounter.text(numberOfLikes);
      })
      .catch((error) => {
        console.log(
            'Failed to fetch like counter for item: ' + itemId +
            ' , with error: ' + error);
      });
}

/**
 * Makes the item submission button visible if the item is not a duplicate; or
 * else, adds a link to the item that already exists.
 *
 * @param { jQuery } submitButton - button used to submit omdb items
 * @param { jQuery } itemCard - card div that displays info about the item found
 * @param { JSON } omdbItem - the item found by omdb API
 */
function updateItemSubmission(submitButton, itemCard, omdbItem) {
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

          submitButton.addClass('d-none');
          itemCard.append($(
              '<p class="card-text text-center">Item already exists on Entertainment Hub!' +
              itemLink + '</p>'));
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
 * Fetches for more entertainment items if the current page is fully scrolled
 * down.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { string } pageCursor - opaque key representing the cursor for the
 *     next page
 */
function updatePagination(favoriteItemIds, pageCursor) {
  $(window).off().scroll(function() {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
      $(window).off('scroll');

      getEntertainmentItems(
          favoriteItemIds, pageCursor, /* Clear items */ false);
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
