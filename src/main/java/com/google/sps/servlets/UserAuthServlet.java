// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
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
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class UserAuthServlet extends HttpServlet {

  private static final String TEXT_CONTENT_TYPE = "text/html";
  private static final String GAME_STAGE_REDIRECT = "/gameStage.html";
  private static final String SLASH_PAGE_REDIRECT = "/index.html";
  private static final String DISPLAY_NAME_PARAMETER = "displayName";
  private static final String EMAIL_PARAMETER = "email";
  private static final String ID_PARAMETER = "id";
  private static final String IMAGE_ID_PARAMETER = "imageID";
  private static final String CURRENT_PAGE_ID_PARAMETER = "currentPageID";
  private static final String PLAYER_PARAMETER = "Player";
  private static UserService userService = UserServiceFactory.getUserService();
  private static User user = userService.getCurrentUser();
  private static String logoutUrl = userService.createLogoutURL(SLASH_PAGE_REDIRECT);
  private static String loginUrl = userService.createLoginURL(GAME_STAGE_REDIRECT);
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(TEXT_CONTENT_TYPE);
    response.getWriter().println(getLoginLogoutLink());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String id = user.getUserId();

      // Creates new entity for user
      Entity entity = newEntity(user, user.getNickname(), id);
    
      datastore.put(entity);
      response.sendRedirect(GAME_STAGE_REDIRECT);
  }

  //displays the link in the DOM
  public String getLoginLogoutLink() {
    String link = "<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>";
    if (userService.isUserLoggedIn()) {
      link = "<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>";
    }
    return link;
  }

  private Entity newEntity(User user, String displayName, String id) {
    
    Entity player = new Entity(PLAYER_PARAMETER);
    player.setProperty(DISPLAY_NAME_PARAMETER, displayName);
    player.setProperty(EMAIL_PARAMETER, user.getEmail());
    player.setProperty(ID_PARAMETER, id);
    // These two will need to be modified as we develop how images/pageIDs are stored
    player.setProperty(IMAGE_ID_PARAMETER, IMAGE_ID_PARAMETER);
    player.setProperty(CURRENT_PAGE_ID_PARAMETER, CURRENT_PAGE_ID_PARAMETER);
    return player;
  }
}
