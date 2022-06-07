package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.util.ArrayList;

public class MahjongClientState {
  private MahjongHand[] playerHands;
  private int myPlayerId;
  public MahjongClientState() {
    playerHands = new MahjongHand[] {
        new MahjongHand(),
        new MahjongHand(),
        new MahjongHand(),
        new MahjongHand()
    };
  }

  public MahjongHand[] getPlayerHands() {
    return playerHands;
  }

  public int getMyPlayerId() {
    return myPlayerId;
  }

  public void setMyPlayerId(int myPlayerId) {
    this.myPlayerId = myPlayerId;
  }
}
