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
      $('<img class="card-img-top mx-auto col-md-10" src="' + omdbItem.Poster +
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
