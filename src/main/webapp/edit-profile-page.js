function loadEditProfileValues() {
  fetch('/profile-data')
      .then((response) => response.json())
      .then((userData) => {
        const profile = userData.profile;

        $('#name').val(profile.name);
        $('#username').val(profile.username);
        $('#bio').val(profile.bio);
      })
      .catch((error) => {
        console.log('Fetching profile data servlet failed: ' + error);
      });
}

function loadNavBar() {
  $(document).ready(function() {
    $('#navbar').load('navbar.html', function() {
      initializeNavBarProfileSection();
    });
  });
}

function addProfileFormValidation() {
  const forms = document.getElementsByClassName('needs-validation');

  Array.prototype.filter.call(forms, (form) => {
    form.addEventListener('submit', function(event) {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add('was-validated');
    }, false);
  }, false);
}
