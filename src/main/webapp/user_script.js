/**
 * Fetches the json from ProfileServlet and displays the values on the Profile
 * Page. Also prepopulates the values on the Edit Profile Page.
 */
function loadProfile() {
  fetch('/profile-data')
      .then((response) => response.json())
      .then((userData) => {
        if(userData.NeedsProfile){
          window.location.replace("/CreateProfilePage.html");
          return;
        }

        const profile = userData.profile;

        const nameSection = document.getElementById('name');
        nameSection.innerHTML = profile.name;
        nameSection.value = profile.name;
        const usernameSection = document.getElementById('username');
        usernameSection.innerHTML = profile.username;
        usernameSection.value = profile.username;
        const bioSection = document.getElementById('bio');
        bioSection.innerHTML = profile.bio;
        bioSection.value = profile.bio;
        const profileImage = document.getElementById("avatar");
        const username = profile.username;
        const avatarLetter = username.charAt(0);
        profileImage.src = "https://icotar.com/avatar/" + avatarLetter;
      })
      .catch((error) => {
        console.log('Fetching profile data servlet failed: ' + error);
      });
  $(document).ready(function() {
    $('#navbar').load('navbar.html', function() {
      $('#navbarProfileSection').addClass('d-none');
    });
  });
}

/**
 * Fetches the items that were liked by user and sends the array to
 * populate the page with the item cards.
 */
function loadFavItems() {
  fetch("/favorite-item")
    .then((response) => response.json())
    .then((favorites) => {
      populateFavoriteItemGrid(favorites);
    })
    .catch((error) => {
      console.log("Fetching favorite item data servlet failed: " + error);
    });
}

/**
 * Creates and populates a grid with item cards.
 *
 * @param { Array } favoriteItems - the array of item ids
 */
function populateFavoriteItemGrid(favoriteItems) {
  const favItemContainer = $("#item-container");
  const MAX_CELLS_PER_ROW = 3;
  let currItemIndex = 0;

  while (currItemIndex < favoriteItems.length) {
    const rowElem = $('<div class="row"></div>');

    for (
      let cell = 0;
      cell < MAX_CELLS_PER_ROW && currItemIndex < favoriteItems.length;
      cell++, currItemIndex++
    ) {
      const item = favoriteItems[currItemIndex];
      const colElem = $('<div class="col-6 col-md-4"></div>');

      fetch(`/itempagedata?itemId=${item}`)
        .then((response) => response.json())
        .then((itemPageData) => {
          colElem.append(createFavoriteItemCard(itemPageData.item));
          rowElem.append(colElem);
        });
    }

    favItemContainer.append(rowElem);
  }
}

/**
 * Creates and populates a grid with item cards.
 *
 * @param entertainmentItem - the entertainment item object
 * @returns card - bootstrap card with item information
 */
function createFavoriteItemCard(entertainmentItem) {
  const card = $('<div class="mt-2" class="card bg-light"></div>');
  card.append(
    $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">')
  );
  card.append(
    $(
      '<a class="stretched-link" href="item-page.html?itemId=' +
        entertainmentItem.uniqueId.value +
        '"></a>'
    )
  );
  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
    $(
      '<h5 class="card-title">' +
        entertainmentItem.title +
        "(" +
        entertainmentItem.releaseDate +
        ")" +
        "</h5>"
    )
  );
  card.append(cardBody);

  return card;
}
