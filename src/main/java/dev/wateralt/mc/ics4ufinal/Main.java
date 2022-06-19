package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.client.Client;
import dev.wateralt.mc.ics4ufinal.client.UIManager;
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
    Window wnd = new Window("CS Mahjong");
    UIManager mgr = new UIManager(wnd);
    wnd.run();
    System.exit(0);
  }
}