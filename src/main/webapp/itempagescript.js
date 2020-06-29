<<<<<<< HEAD
function loadItemPage(){
  sendItemIdToServlet();
  getItemPageComments();
}

function sendItemIdToServlet() {
  const itemId =  getItemId();
  console.log(itemId);
  if (itemId != null) {
    fetch('/itempagedata?itemId=getItemId()');
  }
  else {
    console.log('ItemId is null');
  }
}

function getItemId() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const itemId = urlParams.get('itemId');
  return itemId;
}



=======
function getItemId() {
  const queryString = window.location.search;
  console.log(queryString);
}

>>>>>>> 3ccab2bc0d5465bb8e2ae6e00009a398744dc0b3
async function getItemPageComments() {
  const response = await fetch("/itempagedata");
  const comments = await response.json();
  const commentContainer = document.getElementById("comment-container");
  comments.forEach((commentObject) => {
    const date = new Date(commentObject.timestamp);
    commentContainer.appendChild(
      createListElement(
        commentObject.message + " - " + "(" + date.toUTCString() + ")"
      )
    );
  });
}
function createListElement(comment) {
  const liElement = document.createElement("li");
  liElement.innerText = comment;
  return liElement;
}
