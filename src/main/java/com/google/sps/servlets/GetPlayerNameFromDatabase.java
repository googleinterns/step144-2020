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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that should respond with a JSON String that contains the desired dialogue */
@WebServlet("/get-player-name")
public final class GetPlayerNameFromDatabase extends HttpServlet {
  private static final String TEXT_TO_HTML = "text/html;";
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String LOGGED_OUT_EXCEPTION =
      "Player is currently logged out. Cannot process null user.";
  private static final Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      UserService userService = UserServiceFactory.getUserService();
      PlayerDatabase playerDatabase = new PlayerDatabase(datastore, userService);
      String playerName = playerDatabase.getEntityDisplayName();
      response.setContentType(TEXT_TO_HTML);
      response.getWriter().print(playerName);
    } catch (LoggedOutException e) {
      response.setContentType(TEXT_TO_HTML);
      response.getWriter().print(LOGGED_OUT_EXCEPTION);
    }
  }
}
