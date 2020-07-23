//loads the image upload url to the form
function fetchBlobstoreUrl() {
  fetch('/upload-image')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('my-form');
        messageForm.action = imageUploadUrl;
        messageForm.classList.remove('hidden');
      });
}

function fetchImageHandler() {
    fetch('/image-handler')
        .then(response => response.text())
        .then(message => {
            const NICKNAME_CONTAINER = document.getElementById("nickname-container");
            NICKNAME_CONTAINER.innerHTML = message;          
        })
        .then(imageTagInAnchor => {
            const IMAGE_CONTAINER = document.getElementById("image-container");
            IMAGE_CONTAINER.innerHTML = imageTagInAnchor;                
        })
}

function loadCharacterDesignScripts() {
    fetchBlobstoreUrl();
}

function getImage() {  
  fetch("/image-handler")
      .then(response => response.text())
      .then(message => {
          const NICKNAME_CONTAINER = document.getElementById("nickname-container");
          NICKNAME_CONTAINER.innerText = message;
          var messageArray = message.split("\n");
          const imageContainer = document.getElementById('image-container');
          var blobkey = messageArray[1];
          if (blobkey == "default") {
              createImageElement("images/face.png");
          }
          else {
            fetch('/get-image?blobkey=' + blobkey).then((pic) => {
            createImageElement(pic.url);
          });
          }
  });
}

function createImageElement(pic) {
  let image = document.createElement("img");
  image.src = pic;
  image.id = "player-picture"
  const imageContainer = document.getElementById('image-container');
  imageContainer.append(image);
}

/** Show uploaded image on player preview as soon as it is changed */
function previewImage(event) {
  const playerPicture = document.getElementById('player-picture');
  playerPicture.src = URL.createObjectURL(event.target.files[0]);
  playerPicture.onload = function() {
    URL.revokeObjectURL(playerPicture.src) // free memory
  }
}
