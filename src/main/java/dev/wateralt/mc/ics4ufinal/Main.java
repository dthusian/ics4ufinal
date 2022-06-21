package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.client.Client;
import dev.wateralt.mc.ics4ufinal.client.UIManager;
import dev.wateralt.mc.ics4ufinal.client.uilayers.DebugLayer;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongExtras;
import dev.wateralt.mc.ics4ufinal.client.uilayers.MahjongRenderer;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.yaku.Yaku;
import dev.wateralt.mc.ics4ufinal.server.Server;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Temporary main class. Only used for testing for now.
 */
public class Main {

  public static void main(String[] args) throws IOException {
    if(false) {
      Window wnd = new Window("CS Mahjong");
      UIManager mgr = new UIManager(wnd);
      wnd.run();
      System.exit(0);
    } else {
      MahjongHand hand = new MahjongHand();
      hand.getHidden().addAll(Stream.of(
          "5z",
          "5z",
          "5z",
          "4s",
          "4s",
          "4s",
          "5s",
          "6s"
      ).map(MahjongTile::new).toList());
      hand.getShown().addAll(Stream.of(
          "1p",
          "2p",
          "3p",
          "1m",
          "2m",
          "3m"
      ).map(MahjongTile::new).toList());
      ArrayList<Yaku.YakuEntry> hand2 = Yaku.matchHand(hand);
      System.out.println(hand2);
    }
  }
}