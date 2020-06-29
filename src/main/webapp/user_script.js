
function loadProfile() {
  fetch('/profile-data')
    .then((response) => response.json())
    .then((profile) => {
      var profileContainer = document.getElementById('profile-container');
      commentElement.innerText = profile;
    });
}
