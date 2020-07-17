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

function getFileInput() {
  document.querySelector(".file-input").addEventListener('change', function(e){
    var fileName = document.getElementById("image").files[0].name;
    var nextElement = e.target.nextElementSibling;
    nextElement.innerText = fileName;
  });
}

function loadCharacterDesignScripts() {
    getFileInput();
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
              createImageElement("images/face.jpg");
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
  const imageContainer = document.getElementById('image-container');
  imageContainer.append(image);
}
