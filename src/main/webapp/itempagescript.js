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
