package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.LoggedOutException;
import com.google.sps.data.Player;
import com.google.sps.data.PlayerDatabase;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Allows users to start new game by deleting their previous progress/info */
@WebServlet("/delete-user")
public class DeleteUserServlet extends HttpServlet {
  private static final String JSON_CONTENT_TYPE = "application/json";
  private static final String PLAYER_PARAMETER = "player";
  private static final String ID_PARAMETER = "name";
  private static Gson gson;
  private DatastoreService datastore;
  private UserService userService;
  private PlayerDatabase playerDatabase;

  @Override
  public void init() {
    this.gson = new Gson();
    this.userService = UserServiceFactory.getUserService();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.playerDatabase = new PlayerDatabase(datastore, userService);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Entity currentPlayer = playerDatabase.getCurrentPlayerEntity();
      Key playerEntityKey = currentPlayer.getKey();
      datastore.delete(playerEntityKey);
      deletePlayerFromPlayerDatabase();
    } catch (LoggedOutException e) {
      response.setContentType(JSON_CONTENT_TYPE);
      response.getWriter().println(gson.toJson(e.getMessage()));
    }
  }

  public void deletePlayerFromPlayerDatabase() {
    List<Player> players = playerDatabase.getPlayers();
    Player playerToDelete = null;
    String email = userService.getCurrentUser().getEmail().toString();
    for (Player player : players) {
      if (player.getEmail().equals(email)) {
        playerToDelete = player;
        break;
      }
    }
    players.remove(playerToDelete);
  }
}
