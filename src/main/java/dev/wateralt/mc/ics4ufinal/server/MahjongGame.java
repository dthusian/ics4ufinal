package dev.wateralt.mc.ics4ufinal.server;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.network.GainTilePacket;
import dev.wateralt.mc.ics4ufinal.server.controllers.Controller;

import java.time.Instant;
import java.util.*;

public class MahjongGame {
  public record PlayerState (
      Controller controller,
      MahjongHand hand,
      ArrayList<MahjongTile> discardPile) {
  }

  Deque<MahjongTile> deck;
  // Player 0 is always the dealer
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

  private void createDeckAndDeal(long seed) {
    LinkedList<MahjongTile> genTiles = new LinkedList<>();
    for(int i = 0; i < 4; i++) {
      for(int j = 0; j < 9; j++) {
        genTiles.add(new MahjongTile(i + 1, 'm'));
        genTiles.add(new MahjongTile(i + 1, 'p'));
        genTiles.add(new MahjongTile(i + 1, 's'));
      }
      for(int j = 0; j < 7; j++) {
        genTiles.add(new MahjongTile(i + 1, 'z'));
      }
    }
    deck.clear();
    Random rand = new Random(seed);
    while(!genTiles.isEmpty()) {
      int idx = rand.nextInt(genTiles.size());
      deck.add(genTiles.remove(idx));
    }
    for(int i = 0; i < players.length; i++) {
      players[i].hand().clear();
      for(int j = 0; j < (i == 0 ? 14 : 13); j++) {
        players[i].hand.getHidden().add(deck.remove());
      }
    }
  }

  public void runGame() {
    checkStartConditions();
    Instant timeSeed = Instant.now();
    createDeckAndDeal(timeSeed.getEpochSecond() ^ timeSeed.getNano());
    // Send tiles to all players
    for(int i = 0; i < 4; i++) { // Send packets to player i
      for(int j = 0; j < 4; j++) { // Send packets about player j's hand
        if(i == j) {
          // Reveal data
          for(int k = 0; k < players[j].hand.getLength(); k++) { // Send tile k
            players[i].controller.sendMove(new GainTilePacket(players[j].hand.getHidden().get(k), j));
          }
        } else {
          for(int k = 0; k < players[j].hand.getLength(); k++) { // Send tile k
            players[i].controller.sendMove(new GainTilePacket(MahjongTile.NULL, j));
          }
        }
      }
    }
    // First player has 14 tiles
    int currentPlayerTurn = 0;
    while(true) {

    }
  }

}
