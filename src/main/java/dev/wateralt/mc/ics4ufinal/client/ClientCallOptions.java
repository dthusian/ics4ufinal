package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

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
  HashMap<MahjongTile, MahjongTile[]> chiList;

  public ClientCallOptions(MahjongHand hand, MahjongTile discarded) {
    this.hand = hand;
    this.discarded = discarded;
    pon = hand.canPon(discarded);
    chiList = hand.getChi(discarded);
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

  public HashMap<MahjongTile, MahjongTile[]> getChiList() {
    return chiList;
  }

  public boolean canCall() {
    return pon || kan || chiList.size() > 0;
  }
}
