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

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.sps.data.GameStage;
import com.google.sps.data.GameStageDatabase;
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
  private static final String SOFTWARE_ID = "software-engineering-0";
  private static final String TEST_CONTENT = "hello this is a test";
  private static final Gson gson = new Gson();
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final PlayerDatabase playerDatabase = new PlayerDatabase(datastore);
  private final GameStageDatabase gameStageDatabase = new GameStageDatabase(datastore);

  //variables that make up the id
  private static final String SOFTWARE_ENGINEER_INPUT = "Software Engineer";
  private static final String LEVEL_1 = "1";
  String id1 = SOFTWARE_ENGINEER_INPUT + LEVEL_1;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try{
      GameStage currentGameStage = gameStageDatabase.getGameStage(playerDatabase.getEntityCurrentPageID());
      String dialogue = gson.toJson(currentGameStage.getContent());

      response.setContentType(JSON_CONTENT_TYPE);
      response.getWriter().println(dialogue);
    } catch(Exception e){} 
  }
}
