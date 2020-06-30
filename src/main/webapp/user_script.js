function loadProfile() {
  fetch('/profile-data').then((response) => response.json()).then((profile) => {
    const nameSection = document.getElementById('name');
    nameSection.innerHTML = profile.name;

    const usernameSection = document.getElementById('username');
    usernameSection.innerHTML = profile.username;

    const bioSection = document.getElementById('bio');
    bioSection.innerHTML = profile.bio;
  });
}
