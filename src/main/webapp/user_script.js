/**
 * Fetches the json from ProfileServlet and displays the values on the Profile
 * Page.
 */
function loadProfile() {
  fetch('/profile-data')
    .then((response) => response.json())
    .then((profile) => {
      const nameSection = document.getElementById('name');
      nameSection.innerHTML = profile.name;
      nameSection.value=profile.name;

      const usernameSection = document.getElementById('username');
      usernameSection.innerHTML = profile.username;
      usernameSection.innerHTML = profile.username;

      const bioSection = document.getElementById('bio');
      bioSection.innerHTML = profile.bio;
    })
    .catch((error) => {
      console.log('Fetching profile data servlet failed: ' + error);
    });
}

/**
 * Fetches the json from ProfileServlet and prepoopulates the values on the Edit Profile
 * Page.
 */
function loadPopulatedProfile() {
  fetch('/profile-data')
    .then((response) => response.json())
    .then((profile) => {
      const nameSection = document.getElementById('name');
      nameSection.value=profile.name;

      const usernameSection = document.getElementById('username');
      usernameSection.value = profile.username;
      
      const bioSection = document.getElementById('bio');
      bioSection.value = profile.bio;
    })
    .catch((error) => {
      console.log('Fetching profile data servlet failed: ' + error);
    });
}
