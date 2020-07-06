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
var FETCH_LOGIN = '/login' 
var LOGIN_CONTAINER = 'login-container';

function login(){
    const responsePromise = fetch(FETCH_LOGIN);
    responsePromise.then(response => response.text())
    .then(authInfo => {
        document.getElementById(LOGIN_CONTAINER).innerHTML = authInfo;
    });
}