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
const FINAL_STAGE_REDIRECTION = 'CompletedPath.html';

var dialogueArray;
var dialogueRegex;
var exp;
var threshold = 15;

function loadFunctions() {
  modifyIfFinalStage();
  workPromoButtonSwitch();
  getImage();
  getDialogue();
  getExp();
}

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

function getExp() {
  fetch("/experience")
  .then(response => response.text())
  .then(expString => {
    exp = parseInt(expString);
    console.log("EXPERIENCE: " + exp);
    showExp(exp);
  });
}

function showExp(exp) {
  const expContainer = document.getElementById('exp-container');
  expContainer.innerText = "EXP:" + exp;
}

function changeExp() {
  exp ++;
  workPromoButtonSwitch();
  showExp(exp);
  const params = new URLSearchParams();
  params.append('experience', exp);
  fetch('/experience', {method: 'POST', body: params});
}

function workPromoButtonSwitch() {
  if (exp == threshold) {
    // Changes button to "TRY FOR PROMOTION"
    const promoButton = document.getElementById('promotion-button');
    promoButton.innerText = "TRY FOR PROMOTION";
  } else if (exp == threshold + 1) {
    // Changes button link to promotion.html
    showPromoButton(true, null);
  }
  else {
    // Earning experience points
    showPromoButton(false, changeExp);
  }
}

function showPromoButton(isLinkActive, onclick) {
  const promoButton = document.getElementById('promotion-button');
  const promoLink = document.getElementById('promotion-link');
  if (isLinkActive) {
    promoLink.href = "promotionquiz.html";
  } else {
    promoLink.removeAttribute("href");
  }
  promoButton.onclick = onclick;
  promoButton.innerText = getWorkButtonTask();
}

function getWorkButtonTask() {
  var taskArray = ["WRITE SOME CODE", "ASK MANAGER FOR FEEDBACK",
      "ATTEND A MEETING", "TEST YOUR CODE", "FIX A BUG", "GET SOME COFFEE", "DEBUG A CODE"];
  var randomNum = Math.floor(Math.random() * taskArray.length);
  return taskArray[randomNum];
}
