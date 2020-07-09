/** 
* Fetches the json from ProfileServlet and displays the values on the Profile Page.
 */
function loadProfile() {
  $(document).ready(function() {
    $('#navbar').load('navbar.html', function() {
      $('#navbarDashboardSection').addClass('d-none');
      $('#navbarProfileSection').addClass('d-none');
    });
  });

  fetch('/profile-data').then((response) => response.json()).then((profile) => {
    const nameSection = document.getElementById('name');
    nameSection.innerHTML = profile.name;

    const usernameSection = document.getElementById('username');
    usernameSection.innerHTML = profile.username;

    const bioSection = document.getElementById('bio');
    bioSection.innerHTML = profile.bio;
  }).catch((error) => {
      console.log('Fetching profile data servlet failed: ' + error);
  });
}
