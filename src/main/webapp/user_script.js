/**
 * Fetches the json from ProfileServlet and displays the values on the Profile
 * Page. Also prepopulates the values on the Edit Profile Page.
 */
function loadProfile() {
  fetch('/profile-data')
      .then((response) => response.json())
      .then((profile) => {
        if(profile.NeedsProfile){
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
        profileImage.src = "https://icotar.com/initials/" + avatarLetter;
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
