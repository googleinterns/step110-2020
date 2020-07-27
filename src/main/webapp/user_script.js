/**
 * Fetches the json from ProfileServlet for the user that is logged in and
 * displays the values on the Profile Page. Also prepopulates the values on the
 * Edit Profile Page.
 */
function loadSelfProfilePage() {
  fetch('/profile-data')
      .then((response) => response.json())
      .then((userData) => {
        if (userData.NeedsProfile) {
          window.location.replace('/CreateProfilePage.html');
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

        const profileImage = document.getElementById('avatar');
        profileImage.src = getProfileImageUrl(profile.username);

        loadFavItems(profile.email);
        loadRecommendedUsers(userData.recommendedUsers);
      })
      .catch((error) => {
        console.log('Fetching profile data servlet failed: ' + error);
      });
  $(document).ready(function() {
    $('#navbar').load('navbar.html', function() {
      initializeNavBarProfileSection();
    });
  });
}

/**
 * Fetches the profile page of the user from the query string parameter for
 * username and loads the page with all the relevant information.
 */
function loadUserProfilePage() {
  $(document).ready(function() {
    $('#navbar').load('navbar.html', function() {
      initializeNavBarProfileSection();
    });

    fetch('/profile-data?username=' + getUrlParam('username'))
        .then((response) => response.json())
        .then((profile) => {
          $('#username').text(profile.username);
          $('#bio').text(profile.bio);
          $('#avatar').attr('src', getProfileImageUrl(profile.username));
          loadFavItems(profile.email);
        })
        .catch((error) => {
          console.log('Failed to fetch user profile: ' + error);
        });
  });
}

/**
 * Fetches the items that were liked by user and sends the array to
 * populate the page with the item cards.
 *
 * @param { string } userEmail - the email linked to the favorite items to
 *     display
 */
function loadFavItems(userEmail) {
  fetch('/favorite-item?email=' + userEmail)
      .then((response) => response.json())
      .then((favorites) => {
        populateFavoriteItemGrid(favorites);
      })
      .catch((error) => {
        console.log('Fetching favorite item data servlet failed: ' + error);
      });
}

/**
 * Creates and populates a grid with item cards.
 *
 * @param { Array } favoriteItems - the array of item ids
 */
function populateFavoriteItemGrid(favoriteItems) {
  const favItemContainer = $('#item-container');
  const MAX_CELLS_PER_ROW = 3;
  let currItemIndex = 0;

  while (currItemIndex < favoriteItems.length) {
    const rowElem = $('<div class="row mb-3"></div>');

    for (let cell = 0;
         cell < MAX_CELLS_PER_ROW && currItemIndex < favoriteItems.length;
         cell++, currItemIndex++) {
      const item = favoriteItems[currItemIndex];
      const colElem = $('<div class="col-md-4"></div>');

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
  const card = $('<div class="mt-2 card bg-light"></div>');
  card.append(
      $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));
  card.append(
      $('<a class="stretched-link" href="item-page.html?itemId=' +
        entertainmentItem.uniqueId.value + '"></a>'));

  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
      $('<h5 class="card-title">' + entertainmentItem.title + ' (' +
        entertainmentItem.releaseDate + ')' +
        '</h5>'));
  card.append(cardBody);

  return card;
}

/**
 * Loads the recommended users and displays them in a scrollable div.
 *
 * @param { Array } recommendedUsers - list with the recommended users in
 *     descending order.
 */
function loadRecommendedUsers(recommendedUsers) {
  const usersContainer = $('#recommendedUsersContainer');

  if (recommendedUsers.length === 0) {
    usersContainer.append(
        $('<p class="lead text-center">Like some items to see if there are' +
          ' other users with similar preferences.</p>'));
    return;
  }

  const usersRow = $('<div class="row"</div>')
  usersContainer.append(usersRow);

  const profilePromises = [];

  recommendedUsers.forEach((email) => {
    profilePromises.push(fetch('/profile-data?email=' + email)
                             .then((response) => response.json()));
  });

  Promise.allSettled(profilePromises)
      .then((results) => results.forEach((result) => {
        if (result.rejected) {
          console.log('Failed to fetch recommended user: ' + result.reason);
          return;
        }

        const profile = result.value;

        const userCol = $('<div class="col col-md-3"></div>');
        userCol.append(createProfileCard(profile));

        usersRow.append(userCol);
      }));
}

/**
 * Creates a card div that displays the user avatar and the username.
 *
 * @param { JSON } profile - the JSON object holding data about a user profile
 * @returns { jQuery } card element representing the user
 */
function createProfileCard(profile) {
  const card = $('<div class="card bg-light" id="user-card"></div>');
  card.append(
      $('<img class="card-img-top" src="' +
        getProfileImageUrl(profile.username) + '">'));
  card.append(
      $('<a class="stretched-link" href="user-profile-page.html?username=' +
        profile.username + '"></a>'));

  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
      $('<h5 class="card-title text-center text-wrap">' + profile.username +
        '</h5>'));

  card.append(cardBody);

  return card;
}

/**
 * Returns the profile image Url from a custom avatar website.
 *
 * @param { string } username - the username that is used to generate the avatar
 *     picture, this function assumes that the username is not an empty string
 * @returns { string } Url to the image used as the avatar.
 */
function getProfileImageUrl(username) {
  return 'https://icotar.com/avatar/' + username.charAt(0);
}
