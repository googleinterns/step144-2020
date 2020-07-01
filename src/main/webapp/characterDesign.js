
function fetchBlobstoreUrl() {
  fetch('/upload-image')
      .then((response) => {
        return response.text();
      })
      .then((uploadUrl) => {
        const messageForm = document.getElementById('my-form');
        messageForm.action = uploadUrl;
        messageForm.classList.remove('hidden');
      });
}

function fetchImageHandler() {
    const FILE_UPLOAD_CONTAINER = document.getElementsByName("image");
    if (FILE_UPLOAD_CONTAINER)
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


