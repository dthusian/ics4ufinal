package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.yaku.Yaku;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tracks possible options the client has for calling tiles.
 * Created and updated by Client and read by MahjongExtras.
 */
public class ClientCallOptions {
  MahjongHand hand;
  MahjongTile discarded;

  boolean pon;
  boolean kan;
  boolean ron;
  HashMap<MahjongTile, MahjongTile[]> chiList;

  public ClientCallOptions(MahjongHand hand, MahjongTile discarded) {
    this.hand = hand;
    this.discarded = discarded;
    pon = hand.canPon(discarded);
    chiList = hand.getChi(discarded);
    hand.getHidden().add(discarded);
    ArrayList<Yaku.YakuEntry> possiblyWin = Yaku.matchHand(hand);
    hand.getHidden().remove(discarded);
    ron = possiblyWin != null && possiblyWin.size() > 0;
  }

  public MahjongHand getHand() {
    return hand;
  }

  public MahjongTile getDiscarded() {
    return discarded;
  }

  public boolean canPon() {
    return pon;
  }

  public boolean canKan() {
    return kan;
  }

  public boolean canRon() { return ron; };

  public HashMap<MahjongTile, MahjongTile[]> getChiList() {
    return chiList;
  }

  public boolean canCall() {
    return pon || kan || chiList.size() > 0;
  }
}
