package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.client.Client;
import dev.wateralt.mc.ics4ufinal.client.uilayers.DebugLayer;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongExtras;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongRenderer;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.server.Server;

import java.io.IOException;

/**
 * Temporary main class. Only used for testing for now.
 */
public class Main {

  public static void main(String[] args) throws IOException {
    try {
      Server server = Server.startDebug();
    } catch(IOException ignored) { }

    Window wnd = new Window("CS Mahjong");
    MahjongRenderer render = new MahjongRenderer();
    Client client = Client.startDebug();
    render.setClientState(client.getClientState());
    MahjongExtras extras = new MahjongExtras();
    extras.setRenderer(render);
    extras.setClient(client);
    extras.setClientState(client.getClientState());
    wnd.addLayer(render);
    wnd.addLayer(extras);
    wnd.addLayer(new DebugLayer());
    wnd.run();
    System.exit(0);
  }
}