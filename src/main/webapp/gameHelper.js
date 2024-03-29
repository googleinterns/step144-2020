// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
const FINAL_STAGE_BUTTON_VALUE = 'COMPLETE PATH';
const FINAL_STAGE_REDIRECTION = 'completedPath.html';
const MUSIC_ICON= 'musicIcon';
const UNMUTE_ICON = '<img src="icons/unmute.gif" alt="unmuted icon">';
const MUTE_ICON = '<img src="icons/mute.png" alt="muted icon">';
const HAT_STRING = "HAT";
const COMPANION_STRING = "COMPANION";
const GLASSES_STRING = "GLASSES";
const NONE_EQUIPPED_STRING = "noneEquipped";

const HAT_IMAGE_ID = "accessory-hat";
const GLASSES_IMAGE_ID = "accessory-glasses";
const COMPANION_IMAGE_ID = "accessory-companion";

var PLAYER_NAME_PARAMATER = 'playerNameHere';
var words
var playerNickname;
var dialogueArray;
var isPlayingmusic = true;
var dialogueRegex;
var experience;
var threshold;
var thresholdIncrement;

function getDialogue() {
  getPlayerName();
  const responsePromise = fetch('/game-dialogue');
  responsePromise.then(handleResponse);
}

function handleResponse(response) {
  const textPromise = response.text();
  textPromise.then(addDialogueToDom);
}
 
function addDialogueToDom(textResponse) {
  responseArray = textResponse.split("\n");
  // Divides the response into level and dialogue
  var levelString = responseArray[0];
  // Substring gets rid of the surrounding quotation marks in the string.
  levelString = levelString.substring(levelString.length - 2, levelString.length - 1);
  // Increment is set to the level times five.
  thresholdIncrement = parseInt(levelString) * 5;
  changeThreshold(thresholdIncrement);  
  
  const quoteContainer = document.getElementById('dialogue-container');
  var dialogue = responseArray[1];
  dialogueArray = dialogue.split(";");
  dialogueRegex = 0;
  splitSentenceToWords()
  quoteContainer.innerText = dialogueArray[dialogueRegex];
}

function nextLine() {
  const quoteContainer = document.getElementById('dialogue-container');
  if (dialogueRegex < dialogueArray.length -1){
    dialogueRegex ++;
    splitSentenceToWords()
  }
  quoteContainer.innerText = dialogueArray[dialogueRegex];
}

// loads the image upload url to the form
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

function getImage() {  
  fetch("/image-handler")
      .then(response => response.text())
      .then(message => {
          var messageArray = message.split("\n");
          // messageArray is an array of three parts : 
          // 0) Boolean is user logged in
          // 1) String image blobkey
          // 2) String user display name
          const imageContainer = document.getElementById('image-container');
          var blobkey = messageArray[1];
          if (blobkey == "default") {
              createImageElement("images/face.png");
          } else {
            fetch('/get-image?blobkey=' + blobkey).then((pic) => {
            createImageElement(pic.url);
            });
          }
  });
}

function createImageElement(pic) {
  let image = document.createElement("img");
  image.src = pic;
  image.id = "player-picture";
  const imageContainer = document.getElementById('image-container');
  imageContainer.append(image);
}

function modifyIfFinalStage() {
  fetch("/isFinalStage")
      .then(response => response.text())
      .then(message => {
        let isFinalStage = (message.includes('true'));
        if (isFinalStage) {
          const promotionButton = document.getElementById('promotion-button');
          promotionButton.innerHTML = FINAL_STAGE_BUTTON_VALUE;
          promotionButton.onclick = function() {
            location.href = FINAL_STAGE_REDIRECTION;
          }
        }
      });
}

// playmusic button functions 
function playmusic() {
  const audio = document.getElementById(MUSIC_ICON);
  if(isPlayingmusic) {
    audio.innerHTML = UNMUTE_ICON;
    isPlayingmusic = false;
  } else {
    audio.innerHTML = MUTE_ICON;
    isPlayingmusic = true;
    document.getElementById('musicPlayer').muted=!document.getElementById('musicPlayer').muted;
  }
}

function getExperience() {
  fetch("/experience")
  .then(response => response.text())
  .then(expString => {
    experience = parseInt(expString);
    showExperience(experience);
  });
}

function showExperience(experience) {
  const expContainer = document.getElementById('experience-container');
  expContainer.innerText = "EXP:" + experience;
  workPromoButtonSwitch();
}

function changeExperience() {
  experience ++;
  workPromoButtonSwitch();
  showExperience(experience);
  const params = new URLSearchParams();
  params.append('experiencePoints', experience);
  fetch('/experience', {method: 'POST', body: params});
}

function workPromoButtonSwitch() {
  if (threshold == null) {
    changeThreshold(thresholdIncrement);
  }
  if (experience == threshold) {
    // Changes button to "TRY FOR PROMOTION"
    const promoButton = document.getElementById('promotion-button');
    promoButton.innerText = "TRY FOR PROMOTION";
  } else if (experience == threshold + 1) {
    // Changes button link to promotion.html, and removes button onclick (null)
    showPromoButton(true, tryForPromotion());
  } else {
    // Changes button onclick to increase experience and removes button link
    showPromoButton(false, changeExperience);
  }
}

function showPromoButton(isLinkActive, onclick) {
  const promoButton = document.getElementById('promotion-button');
  if (isLinkActive) {
    promoButton.onclick = tryForPromotion();
  }
  else {
    promoButton.onclick = onclick;
  }
  promoButton.innerText = getWorkButtonTask();
}

function getWorkButtonTask() {
  var taskArray = ["WRITE SOME CODE", "ASK MANAGER FOR FEEDBACK",
      "ATTEND A MEETING", "TEST YOUR CODE", "FIX A BUG", "GET SOME COFFEE", "DEBUG CODE"];
  var randomNum = Math.floor(Math.random() * taskArray.length);
  return taskArray[randomNum];
}

function changeThreshold(increment) {
  fetch("/promotion-threshold")
      .then(response => response.text())
      .then(thresholdString => {
    threshold = parseInt(thresholdString);
    const params = new URLSearchParams();
    threshold = threshold + increment;
    params.append('promotionThreshold', threshold);
    fetch('/promotion-threshold', {method: 'POST', body: params});
  });
}

function tryForPromotion() {
  changeThreshold(thresholdIncrement);
  window.location='promotionquiz.html';
}

/*
 * This function splits sentences from the fetched dialogue into words
 * It then looks for PLAYER_NAME_PARAMETER and replaces it with playerNickname
 */
function splitSentenceToWords() {
  words = dialogueArray[dialogueRegex].split(' ');
  var wordIterator;
  for(wordIterator = 0; wordIterator < words.length; wordIterator++) {
    if(words[wordIterator] == PLAYER_NAME_PARAMATER) {
      words[wordIterator] = playerNickname;
    }
  }
  reconstructWordToSentence(words);
}

/*
 * This function reconstructs words to make a sentence
 */
function reconstructWordToSentence(words) {
  var wordIterator;
  var sentence = '';
  for(wordIterator = 0; wordIterator < words.length;wordIterator++) {
    sentence = sentence + words[wordIterator] + ' ';
  }
  dialogueArray[dialogueRegex] = sentence;
}

function getEquippedAccessories() {
  fetch("/customization").then(handleAccResponse);
}

function handleAccResponse(response) {
  const promise = response.json()
  promise.then(addAccessoriesToDom);
}

function addAccessoriesToDom(playerAccessories) {
  equippedHat = playerAccessories.equippedHat;
  if (equippedHat !== NONE_EQUIPPED_STRING) {
    displayEquippedAccessory(equippedHat, HAT_STRING);
  }
  equippedCompanion = playerAccessories.equippedCompanion;
  if (equippedCompanion !== NONE_EQUIPPED_STRING) {
    displayEquippedAccessory(equippedCompanion, COMPANION_STRING);
  }
  equippedGlasses = playerAccessories.equippedGlasses;
  if (equippedGlasses !== NONE_EQUIPPED_STRING) {
    displayEquippedAccessory(equippedGlasses, GLASSES_STRING);
  }
}

function displayEquippedAccessory(object, type) {
  let accessoryContainer;
  switch(type) {
    case HAT_STRING:
      accessoryContainer = document.getElementById(HAT_IMAGE_ID);
      break;
    case COMPANION_STRING:
      accessoryContainer = document.getElementById(COMPANION_IMAGE_ID);
      break;
    case GLASSES_STRING:
      accessoryContainer = document.getElementById(GLASSES_IMAGE_ID);
      break;
  }

  if (object === NONE_EQUIPPED_STRING || accessoryContainer.name === object.id) {
    object = NONE_EQUIPPED_STRING;
    accessoryContainer.src = null;
    accessoryContainer.name = null;
    accessoryContainer.style.visibility = "hidden";
  } else {
    accessoryContainer.style.position = "absolute";
    accessoryContainer.style.visibility = "visible";
    accessoryContainer.style.zIndex = 3;
    accessoryContainer.style.height = intToDimension(object.height);
    accessoryContainer.style.width = intToDimension(object.width);
    accessoryContainer.style.left = intToDimension(object.xPos);
    accessoryContainer.style.top = intToDimension(object.yPos);
    accessoryContainer.src = object.imageFilePath;
    accessoryContainer.name = object.id;
  }
}

function intToDimension(number) {
  return number.toString() + "px";
}

function getPlayerName() {
  // fetches the players name from a servlet
  // then assigns the name as a variable
  const responsePromise = fetch('/get-player-name');
  responsePromise.then(handleResponsePlayer);
}

function handleResponsePlayer(response) {
  const jsonPromise = response.text();
  jsonPromise.then(addPlayerToDom);
}  

function addPlayerToDom(playerName) {
  var playerNickname = playerName;
}
