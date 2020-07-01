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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a random quote. */
@WebServlet("/software-dialogue")
public final class softwareDialogue extends HttpServlet {
  private static final String TEXT_TO_HTML = "text/html;";
  private static final String END_OF_DIALOGUE = "I have taught you all I know student";

  private int currentIndex = 0;

  private int getCurrentIndex() {
    return this.currentIndex;
  }

  private void setCurrentIndex(int index) {
    this.currentIndex = index;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<String> gameDialogue = new ArrayList<>();//sd = softwareDialogue
    gameDialogue.add("Welcome to CS:Intro to Software Engineering. Here we will teach you the basics. My name is Professor Boolean, and for those who are scared of learning computer science dont worry! The best way to learn about computer Science is bit by bit.");
    gameDialogue.add("The purpose of this class is to teach you what it means to be a software engineer, how to think like a software engineer and help you develop basic skills that will help you in the workforce.");
    gameDialogue.add("First I want to talk about what a Software Engineer is? The pros and cons of being a software engineer? and My experience as a software engineer.");
    gameDialogue.add("Software engineers are computer scientists who use principles and programming languages to build software products, develop games, or handle networkorking systems.");

    response.setContentType(TEXT_TO_HTML);

    if(getCurrentIndex() == gameDialogue.size()){
      response.getWriter().println(END_OF_DIALOGUE);
      setCurrentIndex(0);
    }else if(getCurrentIndex() < gameDialogue.size()){
      response.getWriter().println(gameDialogue.get(getCurrentIndex()));
      setCurrentIndex(getCurrentIndex() + 1);
    }

  }

}
