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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import javax.servlet.annotation.WebServlet;
import com.google.sps.data.GameStage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
 
/** Servlet that returns a random quote. */
@WebServlet("/game-dialogue")
public final class GetGameDialogue extends HttpServlet {
  private static final String TEXT_TO_HTML = "text/html;";
  private static final String TEST_MESSAGE = "dialogue will be displayed here";
  private static final String EMAIL_PARAMETER = "email";    
  private static final String ID_PARAMETER = "id";    
  private static final String IMAGE_PARAMETER = "image";
  private static final String DISPLAY_NAME_PARAMETER = "displayName";
  private static final String UPLOADED_REDIRECT_PARAMETER = "/uploaded.html";
  private static final String PLAYER_QUERY_PARAMETER = "Player";
  private static UserService userService = UserServiceFactory.getUserService();
  private static User user = userService.getCurrentUser();
  private GameStage currentGameStage = new GameStage("software-engineering-0", "hello this is a test");
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(TEXT_TO_HTML);
    response.getWriter().println(currentGameStage.getContent());
  }
}
