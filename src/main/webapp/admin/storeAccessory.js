const filepathRoot = "../images/accessories/";

/** Show uploaded accessory image */
function getImage(filepathStub) {
  const accessoryPicture = document.getElementById('accessory');
  accessoryPicture.src = filepathRoot + filepathStub;
  accessoryPicture.onload = function() {
    URL.revokeObjectURL(accessoryPicture.src) // free memory
  }
  accessoryPicture.onerror = function() {
    document.getElementById("file-exists").innerHTML = "Filepath not found.";
  }
  accessoryPicture.style.zIndex = "3"; // on top of player face and body
  accessoryPicture.style.position = "absolute"; // on top of player face and body
}

function changeAccessory(name, value) {
  value = value.toString() + "px";
  const accessoryPicture = document.getElementById('accessory');
  switch(name) {
    case "height":
      accessoryPicture.style.height = value;
      break;
    case "width":
      accessoryPicture.style.width = value;
      break;
    case "xPos":
      accessoryPicture.style.left = value;
      break;
    case "yPos":
      accessoryPicture.style.top = value;
      break;
  }
}
 