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
var dialogueArray;
var isPlayingmusic = true;
var dialogueRegex;
var experience;
var threshold;
// For now, this is hard coded, will change with new servlet
// https://github.com/googleinterns/step144-2020/issues/171
var newThreshold = 20;
 
function getDialogue() {
  const responsePromise = fetch('/game-dialogue');
  responsePromise.then(handleResponse);
}
 
function handleResponse(response) {
  const textPromise = response.json();
  textPromise.then(addDialogueToDom);
}
 
function addDialogueToDom(dialogue) {
  const quoteContainer = document.getElementById('dialogue-container');
  dialogueArray = dialogue.split(";");
  dialogueRegex = 0;
  quoteContainer.innerText = dialogueArray[dialogueRegex];
}
 
function nextLine() {
  const quoteContainer = document.getElementById('dialogue-container');
  if (dialogueRegex < dialogueArray.length-1){
    dialogueRegex ++;
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
}
 
function changeExperience() {
  experience ++;
  workPromoButtonSwitch();
  showExperience(experience);
  const params = new URLSearchParams();
  params.append('experience', experience);
  fetch('/experience', {method: 'POST', body: params});
}
 
function workPromoButtonSwitch() {
  if (threshold == null) {
    getThreshold();
  }
  if (experience == threshold) {
    // Changes button to "TRY FOR PROMOTION"
    const promoButton = document.getElementById('promotion-button');
    promoButton.innerText = "TRY FOR PROMOTION";
  } else if (experience == threshold + 1) {
    // Changes button link to promotion.html, and removes button onclick (null)
    showPromoButton(true, null);
  } else {
    // Changes button onclick to increase experience and removes button link
    showPromoButton(false, changeExperience);
  }
}
 
function showPromoButton(isLinkActive, onclick) {
  const promoButton = document.getElementById('promotion-button');
  if (isLinkActive) {
    promoButton.onclick = changeThreshold();
  }
  promoButton.onclick = onclick;
  promoButton.innerText = getWorkButtonTask();
}
 
function getWorkButtonTask() {
  var taskArray = ["WRITE SOME CODE", "ASK MANAGER FOR FEEDBACK",
      "ATTEND A MEETING", "TEST YOUR CODE", "FIX A BUG", "GET SOME COFFEE", "DEBUG CODE"];
  var randomNum = Math.floor(Math.random() * taskArray.length);
  return taskArray[randomNum];
}

function getThreshold() {
  fetch("/promotion-threshold")
  .then(response => response.text())
  .then(thresholdString => {
    threshold = parseInt(thresholdString);
    workPromoButtonSwitch();
  });
}

function changeThreshold() {
  const params = new URLSearchParams();
  params.append('promotionThreshold', newThreshold);
  fetch('/promotion-threshold', {method: 'POST', body: params});
  window.location='promotionquiz.html';
}
