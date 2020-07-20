/**
 * Creates the like button for a specific entertainment item and adds all its
 * necessary callbacks.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { JSON } item - the entertainment item that is going to be connected
 *     to the like button
 * @returns { jQuery } a new button ready to be displayed in the entertainment
 *     item card
 */
function createLikeButton(favoriteItemIds, item) {
  const likeButton = $('<button class="btn">Like</button>');

  const likeCounter = $('<span class="badge badge-light ml-2"></span>');
  likeButton.append(likeCounter);

  if (favoriteItemIds.includes(item.uniqueId.value)) {
    switchToUndoLikeButton(favoriteItemIds, item, likeButton, likeCounter);
  } else {
    switchToLikeButton(favoriteItemIds, item, likeButton, likeCounter);
  }

  likeCounter.text(item.numberOfLikes);

  return likeButton;
}

/**
 * Fetches FavoriteItemServlet to add a like to a specific entertainment item.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { JSON } item - the entertainment item associated with the button and
 *     counter
 * @param { jQuery } likeButton - the button that adds a like to the
 *     entertainment item
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function addLikeToEntertainmentItem(
    favoriteItemIds, item, likeButton, likeCounter) {
  const itemId = item.uniqueId.value;

  $.post('/favorite-item?favoriteItemId=' + itemId)
      .done(function() {
        switchToUndoLikeButton(favoriteItemIds, item, likeButton, likeCounter);

        likeCounter.text(++item.numberOfLikes);

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
 * @param { JSON } item - the entertainment item associated with the button and
 *     counter
 * @param { jQuery } likeButton - the button that adds a like to the
 *     entertainment item
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function removeLikeFromEntertainmentItem(
    favoriteItemIds, item, likeButton, likeCounter) {
  const itemId = item.uniqueId.value;

  fetch('/favorite-item?favoriteItemId=' + itemId, {method: 'DELETE'})
      .then(() => {
        switchToLikeButton(favoriteItemIds, item, likeButton, likeCounter);

        likeCounter.text(--item.numberOfLikes);

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
 * @param { JSON } item - the entertainment item associated with the button and
 *     counter
 * @param { jQuery } likeButton - the button that adds a like to the
 *     entertainment item
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function switchToLikeButton(favoriteItemIds, item, likeButton, likeCounter) {
  likeButton.addClass('btn-secondary');
  likeButton.removeClass('btn-dark');
  likeButton.off().click(function() {
    addLikeToEntertainmentItem(favoriteItemIds, item, likeButton, likeCounter);
  });
}

/**
 * Toggles the like button to use undo-like item functionality.
 *
 * @param { Array } favoriteItemIds - the list of entertainment item Ids that
 *     have been liked by the logged in user
 * @param { JSON } item - the entertainment item associated with the button and
 *     counter
 * @param { jQuery } likeButton - the button that adds a like to the
 *     entertainment item
 * @param { jQuery } likeCounter - span div that displays the number of likes an
 *     item has
 */
function switchToUndoLikeButton(
    favoriteItemIds, item, likeButton, likeCounter) {
  likeButton.addClass('btn-dark');
  likeButton.removeClass('btn-secondary');
  likeButton.off().click(function() {
    removeLikeFromEntertainmentItem(
        favoriteItemIds, item, likeButton, likeCounter);
  });
}
