var experience;
var name;
var careerPath;

function onLoadFunctions() {
  fetchBlobstoreUrl();
  getEquippedAccessories();
  getAccessories();
  fillText();
}

function fillText() {
  getName();
  getExperiencePoints();
  getCareerPath();
}

function getName() {
//   fetch("/name")
//   .then(response => response.text())
//   .then(name => {
//   Hard coded for now, will implement Many's servlet.
    const nameContainer = document.getElementById("name-container");
    nameContainer.innerHTML = "name";
//   });
}

function getExperiencePoints() {
  fetch("/experience")
  .then(response => response.text())
  .then(expString => {
  const expContainer = document.getElementById('experience-container');
  expContainer.innerText = "EXP:" + expString;
  });
}

function getCareerPath() {
  fetch("/game-dialogue")
  .then(response => response.text())
  .then(dialogueString => {
  var dialogueArray = dialogueString.split("\n");
  var careerStage = dialogueArray[0];
  var careerPath = careerStage.substring(1, careerStage.length - 2);
  const careerPathContainer = document.getElementById('career-path-container');
  careerPathContainer.innerText = "Career Path:" + careerPath;
  });
}

function saveEquippedAccessories() {
  const equippedHat = document.getElementById("accessory-hat");
  const equippedGlasses = document.getElementById("accessory-glasses");
  const equippedCompanion = document.getElementById("accessory-companion");
  const accParams = new URLSearchParams();
  accParams.append('equippedHat', equippedHat.name);
  accParams.append('equippedGlasses', equippedGlasses.name);
  accParams.append('equippedCompanion', equippedCompanion.name);
  fetch('/customization', {method: 'POST', body: accParams});
}

function getAccessories() {
  fetch("/get-player-accessories")
    .then(response => response.json())
    .then(accessories => {
    createAccessoryGrid(accessories); 
  });
}

function createAccessoryGrid(accessories) {
  const tableDiv = document.getElementById("accessories-table");
  for (var i = 0; i < accessories.length; i ++) {
    addImageToDiv(accessories[i], tableDiv);
  }
}

function addImageToDiv(accessory, div) {
    var button = document.createElement("button");
    // button.class = "accessoryButton";
    button.setAttribute("class",  "accessoryButton");
    button.onclick = function() {displayEquippedAccessory(accessory, accessory.type)};
    imgHTML = "<img id=\"gridAccessory\"src=\"" + accessory.imageFilePath + "\">";
    button.innerHTML = imgHTML
    div.appendChild(button);
}

/** Show uploaded image on player preview as soon as it is changed */
function previewImage(event) {
  const playerPicture = document.getElementById('player-picture');
  playerPicture.src = URL.createObjectURL(event.target.files[0]);
  playerPicture.onload = function() {
    URL.revokeObjectURL(playerPicture.src) // free memory
  }
}
