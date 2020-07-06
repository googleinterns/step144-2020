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

import com.google.sps.data.GameStage;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a random quote. */
@WebServlet("/game-dialogue")
public final class GetGameDialogue extends HttpServlet {
  private static final String TEXT_TO_HTML = "text/html;";
  private static final String SOFTWAREID = "software-engineering-0";
  private static final String TESTCONTENT = "hello this is a test";

  // TODO: GameStage will change and be query from a database that will be implemented
  private GameStage currentGameStage = new GameStage(SOFTWAREID, TESTCONTENT);

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(TEXT_TO_HTML);
    response.getWriter().println(currentGameStage.getContent());
  }
}
