package dev.wateralt.mc.ics4ufinal;

public class Main {
  public static void main(String[] args) {
    UIWindow wnd = new UIWindow("LWJGL Tech Test");
    wnd.setRenderer(new MahjongRenderer());
    wnd.run();
  }
}