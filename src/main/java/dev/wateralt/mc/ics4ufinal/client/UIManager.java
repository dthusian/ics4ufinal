package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongExtras;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongRenderer;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MenuUI;
import dev.wateralt.mc.ics4ufinal.common.Logger;
import dev.wateralt.mc.ics4ufinal.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Basically manages the entire UI flow for game
 */
public class UIManager {
  public static int MENU_MAIN = 0;
  public static int MENU_LOBBY = 1;
  public static int MENU_INGAME = 2;

  Window wnd;
  Client client;
  Server server;
  Logger logs;
  MahjongRenderer renderer;
  MahjongExtras extras;

  public UIManager(Window wnd) {
    this.wnd = wnd;
    client = null;
    server = null;
    renderer = new MahjongRenderer();
    extras = new MahjongExtras();
    wnd.addLayer(renderer);
    wnd.addLayer(new MenuUI(this));
  }

  public void connectToGame(String uri) throws IOException {
    client = new Client(uri);
    logs.info("Connecting to %s", uri);
  }

  public void hostGame() throws IOException {
    server = new Server(34567);
    client = new Client("127.0.0.1:34567");
    logs.info("Hosted game on port 34567");
  }

  public void disconnect() throws IOException {
    if(server != null) server.stop();
    client.close();
    logs.info("Closing game");
  }

  public Client getClient() {
    return client;
  }

  public Server getServer() {
    return server;
  }
}
