package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class UserAuthServlet extends HttpServlet {

  private static final String TEXT_CONTENT_TYPE = "text/html";
  private static final String GAME_STAGE_REDIRECT = "/characterDesign.html";
  private static final String SLASH_PAGE_REDIRECT = "/index.html";
  private static final String DISPLAY_NAME_PARAMETER = "displayName";
  private static final String EMAIL_PARAMETER = "email";
  private static final String ID_PARAMETER = "id";
  private static final String IMAGE_ID_PARAMETER = "imageID";
  private static final String CURRENT_PAGE_ID_PARAMETER = "currentPageID";
  private static final String PLAYER_PARAMETER = "Player";
  private UserService userService = UserServiceFactory.getUserService();
  private User user = userService.getCurrentUser();
  private static String logoutUrl = userService.createLogoutURL(SLASH_PAGE_REDIRECT);
  private static String loginUrl = userService.createLoginURL(GAME_STAGE_REDIRECT);

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(TEXT_CONTENT_TYPE);
    response.getWriter().println(getLoginLogoutLink());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect(GAME_STAGE_REDIRECT);
  }

  private String getLoginLogoutLink() {
    String link = "<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>";
    if (userService.isUserLoggedIn()) {
      link = "<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>";
    }
    return link;
  }
}
