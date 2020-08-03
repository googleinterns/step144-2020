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

var PLAYER_NAME_PARAMATER = 'playerNameHere';
var words
var playerNickname = 'Intern';
var dialogueArray;
var isPlayingmusic = true;
var dialogueRegex;
var experience;
var threshold;
var thresholdIncrement;

function getDialogue() {
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
  levelString = levelString.substring(1, levelString.length - 1);
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

//play button functions
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
