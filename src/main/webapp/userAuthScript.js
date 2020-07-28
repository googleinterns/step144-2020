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

/*
* Purpose: recieves HTTP promise response from login.html, 
* Once login redirects to comment.html
*/
var FETCH_LOGIN = '/login';
var LOGIN_CONTAINER = 'login-container';
var BUTTON_CONTAINER = 'button-container';

function login() {
  const responsePromise = fetch(FETCH_LOGIN);
  responsePromise.then(handleResponse);
}

function handleResponse(response) {
  const textPromise = response.text();
  textPromise.then(addDialogueToDom);
}

function addDialogueToDom(authInfo) {
  const quoteContainer = document.getElementById(LOGIN_CONTAINER);
  if (authInfo.includes("Logout")) {
    loadOptions(authInfo);
  }
  quoteContainer.innerHTML = authInfo;
  
}

function loadOptions (logoutLink) {
  var lastStageLink = createButtonWithLink("Return to Last Stage", "gameStage.html", null);
  var newGameLink = createButtonWithLink("New Game", "characterDesign.html", newGame);

  const buttonContainer = document.getElementById(BUTTON_CONTAINER);
  buttonContainer.appendChild(lastStageLink);
  buttonContainer.appendChild(document.createElement("br"));
  buttonContainer.appendChild(newGameLink);
  buttonContainer.appendChild(document.createElement("br"));
}

function createButtonWithLink(text, linkHref, onclick) {
  let button = document.createElement('button');
  button.innerText = text;
  button.className = "step-button";
  let link = document.createElement('a');
  link.href = linkHref;
  link.appendChild(button);
  return link;
}

function newGame() {
  //Deletes player tied to email before user can create new player
  fetch('/delete-user', {method: 'POST'});
}
