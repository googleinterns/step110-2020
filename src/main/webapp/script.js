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

        for (entertainmentItem of entertainmentItemsList) {
          entertainmentItemsContainer.append(
              createEntertainmentItemListElem(entertainmentItem));
        }
      })
      .catch((error) => {
        console.log('failed to fetch entertainment items: ' + error);
      });
}

/**
 * Fetches BlobstoreServlet to get upload Url and then display submission form
 * when it becomes available.
 */
function getEntertainmentItemForm() {
  fetch('/blobstore-upload?servletRedirectUrl=/item-submission')
      .then((response) => response.text())
      .then((imageUploadUrl) => {
        const entertainmentItemForm = $('#entertainmentItemForm');
        entertainmentItemForm.removeClass('hidden');
        entertainmentItemForm.attr('action', imageUploadUrl);
      })
      .catch((error) => {
        console.log('failed to fetch upload Url from Blobstore: ' + error);
      });
}

/**
 * Creates a list element that displays information about
 * a specific entertainment item.
 *
 * @param { EntertainmentItem } entertainmentItem the entertainment item whose
 *     data will be displayed in the list element
 * @returns { jQuery } list element representing entertainment item
 */
function createEntertainmentItemListElem(entertainmentItem) {
  const entertainmentItemElem = $('<li></li>');
  const entertainmentItemLink =
      $('<a href="item-page.html/' + entertainmentItem.uniqueID + '"></a>');

  entertainmentItemLink.append($('<h3>' + entertainmentItem.title + '</h3>'));
  entertainmentItemLink.append(
      $('<p>' + entertainmentItem.description + '</h3>'));
  entertainmentItemLink.append(
      $('<img src="' + entertainmentItem.imageUrl + '"/>'));

  entertainmentItemElem.append(entertainmentItemLink);

  return entertainmentItemElem;
}
