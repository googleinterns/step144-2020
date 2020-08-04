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
import com.google.gson.Gson;
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that should respond with a JSON String that contains the desired dialogue */
@WebServlet("/game-dialogue")
public final class GetGameDialogue extends HttpServlet {
  private static final String TEXT_TO_HTML = "text/html;";
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String LOGGED_OUT_EXCEPTION = "It appears that you have not logged in";
  private static final String NUMBER_FORMAT_EXCEPTION = "Could not parse integer from level id";
  private static final Gson gson = new Gson();
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final PlayerDatabase playerDatabase = new PlayerDatabase(datastore);
  private final GameStageDatabase gameStageDatabase = new GameStageDatabase(datastore);

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      GameStage currentGameStage =
          gameStageDatabase.getGameStage(playerDatabase.getEntityCurrentPageID());
      String dialogue = currentGameStage.getContent();
      String id = currentGameStage.getID();
      String level = id.substring(id.length() - 1);
      // Checks that the Level String can be parsed to an int without throwing an exception.
      int levelInt = Integer.parseInt(level);
      String levelJson = gson.toJson(id);

      response.setContentType(TEXT_TO_HTML);
      response.getWriter().println(levelJson);
      response.getWriter().println(dialogue);
    } catch (LoggedOutException e) {
      response.setContentType(TEXT_TO_HTML);
      response.getWriter().println(LOGGED_OUT_EXCEPTION);
    } catch (NumberFormatException e) {
      response.getWriter().println(NUMBER_FORMAT_EXCEPTION);
    }
  }
}
