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
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that responds with true if user is on a final stage in a path, false if not */
@WebServlet("/isFinalStage")
public final class IsFinalStage extends HttpServlet {
  private static final String HTML_CONTENT_TYPE = "text/html;";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      UserService userService = UserServiceFactory.getUserService();
      PlayerDatabase playerDatabase = new PlayerDatabase(datastore, userService);
      GameStageDatabase gameStageDatabase = new GameStageDatabase(datastore);
      GameStage currentGameStage =
          gameStageDatabase.getGameStage(playerDatabase.getEntityCurrentPageID());
      boolean isFinalStage = currentGameStage.isFinalStage();
      response.setContentType(HTML_CONTENT_TYPE);
      response.getWriter().println(Boolean.toString(isFinalStage));
    } catch (LoggedOutException e) {
      response.setContentType(HTML_CONTENT_TYPE);
      response.getWriter().println(e.getMessage());
    }
  }
}
