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
  likeButton.off().one('click', function() {
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
  likeButton.off().one('click', function() {
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
