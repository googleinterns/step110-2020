function loadProfile() {
  fetch('/profile-data').then((response) => response.json()).then((profile) => {
    var nameSection = document.getElementById('name');
    nameSection.innerHTML = profile.name;

    var usernameSection = document.getElementById('username');
    usernameSection.innerHTML = profile.username;

    var bioSection = document.getElementById('bio');
    bioSection.innerHTML = profile.bio;
  });
}
