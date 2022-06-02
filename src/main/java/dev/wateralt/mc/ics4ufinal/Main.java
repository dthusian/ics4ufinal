package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.client.uilayers.DebugLayer;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongRenderer;
import dev.wateralt.mc.ics4ufinal.client.Window;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    Window wnd = new Window("LWJGL Tech Test");
    wnd.addLayer(new MahjongRenderer());
    wnd.addLayer(new DebugLayer());
    wnd.run();
  }
}