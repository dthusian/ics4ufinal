package dev.wateralt.mc.ics4ufinal;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    try {
      UIWindow wnd = new UIWindow("LWJGL Tech Test");
      MahjongRenderer renderer = new MahjongRenderer(wnd.getWidth(), wnd.getHeight());
      wnd.setRenderer(renderer);
      wnd.run();
    } catch(IOException err) {
      System.out.println(err);
    }
  }
}