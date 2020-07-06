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

import com.google.sps.data.Player;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns a random quote. */
@WebServlet("/software-dialogue")
public final class softwareDialogue extends HttpServlet {
  private static final String TEXT_TO_HTML = "text/html;";
  private static final String END_OF_DIALOGUE = "I have taught you all I know student";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Player admin = new Player("admin" , "admin@google.com");
    
    response.getWriter().println(admin.getDialogue());
    response.setContentType(TEXT_TO_HTML);
  }

}
