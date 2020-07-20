/**
 * Fetches the json from ProfileServlet and displays the values on the Profile
 * Page. Also prepopulates the values on the Edit Profile Page.
 */
function loadProfile() {
  fetch('/profile-data')
    .then((response) => response.json())
    .then((profile) => {
      if (profile.NeedsProfile) {
        window.location.replace("/CreateProfilePage.html");
        return;
      }
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

function loadFavItems() {
  fetch('/favorite-item')
    .then((response) => response.json())
    .then((favorites) => {
      var itemIdArray = new Array();
      const itemId = favorites;
      itemIdArray.push(itemId);
      itemIdArray.forEach(getItem);
    })
    .catch((error) => {
        console.log('Fetching favorite item data servlet failed: ' + error);
    });
}

function getItem(item) {
  if (item !== '') {
    fetch(`/itempagedata?itemId=${item}`)
      .then((response) => response.json())
      .then((itemPageData) => {
        createSelectedItemCard(itemPageData.item);
      })
      .catch((error) => {
        console.log('Fetching favorite item data servlet failed: ' + error);
      });
  }
}

async function createSelectedItemCard(entertainmentItem) {
  const card = $('<div class="mt-2" class="card bg-light"></div>');
  card.append(
    $('<img class="card-img-top" src="' + entertainmentItem.imageUrl + '">'));
  const cardBody = $('<div class="card-body"></div>');
  cardBody.append(
    $('<h5 class="card-title">' + entertainmentItem.title + '(' +
      entertainmentItem.releaseDate + ')' +
      '</h5>'));
  cardBody.append(
    $('<h5 class="card-title">' + entertainmentItem.genre + '</h5>'));
  cardBody.append(
    $('<p class="card-text"><b>Description: </b>' +
      entertainmentItem.description + '</p>'));

  card.append(cardBody);
  const itemContainer = $('#item-container');
  itemContainer.append(card);
}