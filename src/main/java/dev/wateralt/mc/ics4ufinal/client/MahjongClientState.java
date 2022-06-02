package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.util.ArrayList;

public class MahjongClientState {
  private ArrayList<ArrayList<MahjongTile>> opponentHands;

  private ArrayList<MahjongTile> yourHand;

  public ArrayList<ArrayList<MahjongTile>> getOpponentHands() {
    return opponentHands;
  }
}
