async function getItemPageComments() {
  const response = await fetch("/itempagedata");
  const comments = await response.json();
  const commentContainer = document.getElementById("comment-container");
  comments.forEach((commentObject) => {
    commentContainer.appendChild(
      createListElement(
        commentObject.message + " " + "(" + commentObject.timestamp + ")"
      )
    );
  });
}
function createListElement(comment) {
  const liElement = document.createElement("li");
  liElement.innerText = comment;
  return liElement;
}
