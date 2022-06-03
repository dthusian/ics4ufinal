package dev.wateralt.mc.ics4ufinal.server;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class MahjongGame {
  public record PlayerState (
      Controller controller,
      MahjongHand hand,
      ArrayList<MahjongTile> discardPile) {
  }

  Queue<MahjongTile> deck;
  PlayerState[] players;

  public MahjongGame() {
    deck = new ArrayDeque<>();
    players = new PlayerState[4];
  }

  public void addPlayer(Controller ctl) {
    boolean inserted = false;
    for(int i = 0; i < players.length; i++) {
      if(players[i] == null) {
        players[i] = new PlayerState(ctl, null, new ArrayList<>());
        inserted = true;
        break;
      }
    }
    if(!inserted) {
      throw new IllegalStateException("Players already full, cannot add");
    }
  }

  public void checkStartConditions() {
    for(int i = 0; i < players.length; i++) {
      if(players[i] == null) {
        throw new IllegalStateException("Cannot start without 4 players");
      }
    }
  }

  public void runGame() {}

}
