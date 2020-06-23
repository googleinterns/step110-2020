/**
 * Fetches entertainment items from EntertainmentItemServlet
 * and loads them into the DOM.
 */
function getEntertainmentItems() {
  fetch(
      '/entertainment-item-data?searchValue=' + $('#searchValue').val() +
      '&sortingMode=' + $('#sortingMode').val())
      .then((response) => response.json())
      .then((entertainmentItemsList) => {
        const entertainmentItemsContainer = $('#entertainmentItemsContainer');
        entertainmentItemsContainer.empty();

        for (entertainmentItem of entertainmentItemsList) {
          entertainmentItemsContainer.append(createEntertainmentItem(
              entertainmentItem.title, entertainmentItem.description,
              entertainmentItem.imageURL));
        }
      })
      .catch((error) => {
        console.log('failed to fetch entertainment items: ' + error);
      });
}

/**
 * Fetches Blobstore Servlet to get upload URL and then display submission form
 * when it becomes available.
 */
function getEntertainmentItemForm() {
  fetch('/blobstore-upload')
      .then((response) => response.text())
      .then((imageUploadURL) => {
        const entertainmentItemForm = $('#entertainmentItemForm');
        entertainmentItemForm.removeClass('hidden');
        entertainmentItemForm.attr('action', imageUploadURL);
      })
      .catch((error) => {
        console.log('failed to fetch upload URL from Blobstore: ' + error);
      });
}

/**
 * Creates a list element that displays information about
 * a specific entertainment item.
 *
 * @param { string } title - title associated with the entertainment item
 * @param { string } description - description associated with the entertianment
 *     item
 * @param { string } imageURL - URL to image stored in blobstore
 * @returns { jQuery } list element representing entertainment item
 */
function createEntertainmentItem(title, description, imageURL) {
  const entertainmentItemElem = $('<li></li>');
  entertainmentItemElem.append($('<h3>' + title + '</h3>'));
  entertainmentItemElem.append($('<p>' + description + '</h3>'));
  entertainmentItemElem.append($('<img src="' + imageURL + '"/>'));

  return entertainmentItemElem;
}