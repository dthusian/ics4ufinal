package dev.wateralt.mc.ics4ufinal.server;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.GainTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.server.controllers.Controller;

import java.time.Instant;
import java.util.*;

public class MahjongGame {
  public class PlayerState {
    private Controller controller;
    private MahjongHand hand;
    private ArrayList<MahjongTile> discardPile;
    private String name;

    public PlayerState(Controller ctl, MahjongHand hnd, ArrayList<MahjongTile> es, String name2) {
      controller = ctl;
      hand = hnd;
      discardPile = es;
      name = name2;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public ArrayList<MahjongTile> getDiscardPile() {
      return discardPile;
    }

    public void setDiscardPile(ArrayList<MahjongTile> discardPile) {
      this.discardPile = discardPile;
    }

    public Controller getController() {
      return controller;
    }

    public void setController(Controller controller) {
      this.controller = controller;
    }

    public MahjongHand getHand() {
      return hand;
    }

    public void setHand(MahjongHand hand) {
      this.hand = hand;
    }
  }

  Deque<MahjongTile> deck;
  // Player 0 is always the dealer
  PlayerState[] players;
  int dealer;

  public MahjongGame() {
    deck = new ArrayDeque<>();
    players = new PlayerState[4];
    dealer = 0;
  }

  // Setup functions

  public void addPlayer(Controller ctl) {
    boolean inserted = false;
    for(int i = 0; i < players.length; i++) {
      if(players[i] == null) {
        players[i] = new PlayerState(ctl, null, new ArrayList<>(), "Player " + (i + 1));
        players[i].controller.initialize(players[i]);
        inserted = true;
        break;
      }
    }
    if(!inserted) {
      throw new IllegalStateException("Players already full, cannot add");
    }
  }

  public int getDealer() {
    return dealer;
  }

  public void setDealer(int dealer) {
    this.dealer = dealer;
  }

  public void checkStartConditions() {
    for(int i = 0; i < players.length; i++) {
      if(players[i] == null) {
        throw new IllegalStateException("Cannot start without 4 players");
      }
    }
  }

  // Run game functions

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
      players[i].hand.clear();
      for(int j = 0; j < 13; j++) {
        players[i].hand.getHidden().add(deck.remove());
      }
    }
  }

  private void broadcast(Packet p) {
    for(int i = 0; i < players.length; i++) {
      players[i].controller.send(p);
    }
  }

  private void broadcastExcept(Packet pub, int specialPlayer, Packet priv) {
    for(int i = 0; i < players.length; i++) {
      if(i == specialPlayer) {
        players[i].controller.send(priv);
      } else {
        players[i].controller.send(pub);
      }
    }
  }

  private void singleSend(Packet p, int player) {
    players[player].controller.send(p);
  }

  public void runGame() {
    checkStartConditions();
    Instant timeSeed = Instant.now();
    createDeckAndDeal(timeSeed.getEpochSecond() ^ timeSeed.getNano());
    // Send tiles to all players
    for(int j = 0; j < 4; j++) { // Send packets about player j's hand
      for(int k = 0; k < players[j].hand.getLength(); k++) { // Send tile k
        broadcastExcept(new GainTilePacket(MahjongTile.NULL, j), j, new GainTilePacket(players[j].hand.getHidden().get(k), j));
      }
    }

    int currentPlayerTurn = dealer;
    while(true) {
      // Deal
      MahjongTile newTile = deck.remove();
      players[currentPlayerTurn].hand.getHidden().add(newTile);
      broadcastExcept(new GainTilePacket(MahjongTile.NULL, currentPlayerTurn), currentPlayerTurn, new GainTilePacket(newTile, currentPlayerTurn));

      // Deal with packets
      while(true) {
        Packet recved = players[currentPlayerTurn].controller.receive(500);
        if (recved.getId() == DiscardTilePacket.ID) {

        }
      }

      // Next player turn
      currentPlayerTurn++;
      currentPlayerTurn %= 4;
    }
  }

}
