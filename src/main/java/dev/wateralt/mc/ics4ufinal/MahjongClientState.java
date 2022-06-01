package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.pod.MahjongTile;
import dev.wateralt.mc.ics4ufinal.pod.Tuple2;

import java.util.ArrayList;

public class MahjongClientState {
  private ArrayList<ArrayList<MahjongTile>> opponentHands;

  private ArrayList<MahjongTile> yourHand;

  public ArrayList<ArrayList<MahjongTile>> getOpponentHands() {
    return opponentHands;
  }
}
