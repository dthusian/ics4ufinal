package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.client.UIManager;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.yaku.Yaku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Temporary main class. Only used for testing for now.
 */
public class Main {

  public static void main(String[] args) {
    Window wnd = new Window("CS Mahjong");
    UIManager mgr = new UIManager(wnd);
    wnd.run();
    System.exit(0);
  }
}