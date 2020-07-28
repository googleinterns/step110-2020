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
 * link and a "Logout" link to the navbar, if the user is not logged in then it
 * adds a link to login.
 */
function initializeNavBarProfileSection() {
  fetch('/login')
      .then((response) => response.json())
      .then((loginResponse) => {
        const profileLinks = $('#profileLinks');
        const logLinks = $('#logLinks');

        if (loginResponse.isUserLoggedIn) {
          profileLinks.append($(
              '<a class="nav-link text-light" href="/ProfilePage.html">Profile</a>'));
          logLinks.append(
              $('<a class="nav-link text-light" href="' +
                loginResponse.LogoutURL + '">Logout</a>'));
        } else {
          profileLinks.append(
              $('<a class="nav-link text-light" href="' +
                loginResponse.LoginURL + '">Login</a>'));
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
function initializeDashboard() {
  Promise
      .allSettled([
        fetch(
            '/dashboard?searchValue=' + $('#searchValue').val() +
            '&sortType=' + $('#sortType').val())
            .then((response) => response.json()),
        fetch('/favorite-item').then((response) => response.json())
      ])
      .then((results) => {
        dashboardResult = results[0];
        favResult = results[1];

        if (dashboardResult.status === 'rejected') {
          console.log(
              'Failed to fetch Entertainment Items from DashboardServlet: ' +
              dashboardResult.reason);
          return;
        }

        const entertainmentItems = dashboardResult.value;
        const favoriteItemIds =
            favResult.status === 'fulfilled' ? favResult.value : [];

        setupSeachInputCallback(favoriteItemIds);
        setupSortSelectorCallback(favoriteItemIds);
        updateDashboardItems(entertainmentItems, favoriteItemIds);
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
        entertainmentItem.uniqueId.value + '"></a>'));

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
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight) {
      $(window).off('scroll');

      getEntertainmentItems(
          favoriteItemIds, pageCursor,
          /* Clear items */ false);
    }
  });
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
