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
var i;
function loadFunctions() {
  modifyIfFinalStage();
  getImage();
  getDialogue();
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
  i = 0;
  quoteContainer.innerText = dialogueArray[i];
}

function nextLine() {
  const quoteContainer = document.getElementById('dialogue-container');
  if (i < dialogueArray.length-1){
    i ++;
  }
  quoteContainer.innerText = dialogueArray[i];
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
