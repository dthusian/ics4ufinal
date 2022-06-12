package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.client.Client;
import dev.wateralt.mc.ics4ufinal.client.MahjongClientState;
import dev.wateralt.mc.ics4ufinal.client.uilayers.DebugLayer;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongRenderer;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.server.Server;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
  static Thread serverThread;

  public static void main(String[] args) throws IOException {
    serverThread = new Thread(Main::serverMain);
    serverThread.start();

    Window wnd = new Window("CS Mahjong");
    MahjongRenderer render = new MahjongRenderer();
    Client client = Client.startDebug();
    render.setClientState(client.getClientState());
    wnd.addLayer(render);
    wnd.addLayer(new DebugLayer());
    wnd.run();
    System.exit(0);
  }

  public static void serverMain() {
    try {
      Server server = Server.startDebug();
      server.run();
    } catch(IOException ignored) { }
  }
}