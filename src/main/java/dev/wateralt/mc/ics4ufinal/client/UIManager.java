package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.client.uilayers.LobbyUI;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongExtras;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongRenderer;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MenuUI;
import dev.wateralt.mc.ics4ufinal.common.Logger;
import dev.wateralt.mc.ics4ufinal.server.Server;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

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
    logs = new Logger(this);
    wnd.addLayer(renderer);
    wnd.addLayer(new MenuUI(this));
  }

  private void toMahjongUI() {
    renderer.setClientState(client.getClientState());
    wnd.removeLayer("MenuUI");
    MahjongExtras extras = new MahjongExtras();
    extras.setClient(client);
    extras.setRenderer(renderer);
    LobbyUI lobby = new LobbyUI(this);
    lobby.setClientState(client.getClientState());
    wnd.addLayer(extras);
    wnd.addLayer(lobby);
    wnd.setCursor(false);
  }

  public void connectToGame(String uri) throws IOException {
    client = new Client(uri);
    logs.info("Connecting to %s", uri);
    toMahjongUI();
  }

  public void hostGame(int nBots) throws IOException {
    server = new Server(34567, nBots);
    client = new Client("127.0.0.1:34567");
    logs.info("Hosted game on port 34567");
    toMahjongUI();
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
