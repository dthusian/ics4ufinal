package dev.wateralt.mc.ics4ufinal.server;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.network.DiscardTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.GainTilePacket;
import dev.wateralt.mc.ics4ufinal.common.network.NoActionPacket;
import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.server.controllers.Controller;
import dev.wateralt.mc.ics4ufinal.server.controllers.NullController;

import java.time.Instant;
import java.util.*;

/**
 * Master class controlling the Mahjong game flow. Manages all the moves and state of the game.
 */
public class MahjongGame {
  /**
   * Represents all the state of one player.
   */
  public static class PlayerState {
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

    /**
     *
     * @return the name of the player
     */
    public String getName() {
      return name;
    }

    /**
     *
     * @param name the new name of the player
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     *
     * @return the discard pile of the player
     */
    public ArrayList<MahjongTile> getDiscardPile() {
      return discardPile;
    }

    /**
     *
     * @param discardPile the new discard pile of the player
     */
    public void setDiscardPile(ArrayList<MahjongTile> discardPile) {
      this.discardPile = discardPile;
    }

    /**
     *
     * @return the controller backing the player
     */
    public Controller getController() {
      return controller;
    }

    /**
     *
     * @param controller the controller backing the player
     */
    public void setController(Controller controller) {
      this.controller = controller;
    }

    /**
     *
     * @return the player's hand
     */
    public MahjongHand getHand() {
      return hand;
    }

    /**
     *
     * @param hand the player's new hand
     */
    public void setHand(MahjongHand hand) {
      this.hand = hand;
    }
  }

  Deque<MahjongTile> deck;
  PlayerState[] players;
  int dealer;

  /**
   * Initializes a new game. The game needs players in order to begin.
   */
  public MahjongGame() {
    deck = new ArrayDeque<>();
    players = new PlayerState[4];
    dealer = 0;
  }

  // Setup functions

  /**
   * Adds a player to the game. Throws an exception if more than 4 players are added.
   * @param ctl
   */
  public void addPlayer(Controller ctl) {
    boolean inserted = false;
    for(int i = 0; i < players.length; i++) {
      if(players[i] == null) {
        players[i] = new PlayerState(ctl, new MahjongHand(), new ArrayList<>(), "Player " + (i + 1));
        players[i].controller.initialize(this, i);
        inserted = true;
        break;
      }
    }
    if(!inserted) {
      throw new IllegalStateException("Players already full, cannot add");
    }
  }

  /**
   *
   * @return the index of player that is responsible for dealing.
   */
  public int getDealer() {
    return dealer;
  }

  /**
   *
   * @param dealer the new player responsible for dealing.
   */
  public void setDealer(int dealer) {
    this.dealer = dealer;
  }

  /**
   * Throws an error if the game cannot start because some precondition isn't met.
   */
  public void checkStartConditions() {
    for (PlayerState player : players) {
      if (player == null) {
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
    for (PlayerState player : players) {
      player.hand.clear();
      for (int j = 0; j < 13; j++) {
        player.hand.getHidden().add(deck.remove());
      }
    }
  }

  private void broadcast(Packet p) {
    for (PlayerState player : players) {
      player.controller.send(p);
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

  // Player did something bad and is being un-alived
  private void unalivePlayer(PlayerState pl) {
    pl.controller = new NullController();
  }

  /**
   * Starts the game! Blocks the calling thread until the game is finished.
   */
  public void runGame() {
    checkStartConditions();
    Instant timeSeed = Instant.now();
    createDeckAndDeal(timeSeed.getEpochSecond() ^ timeSeed.getNano());
    // Send tiles to all players
    for(int j = 0; j < 4; j++) { // Send packets about player j's hand
      for(int k = 0; k < players[j].hand.getHidden().size(); k++) { // Send tile k
        broadcastExcept(new GainTilePacket(MahjongTile.NULL, j), j, new GainTilePacket(players[j].hand.getHidden().get(k), j));
      }
    }

    int currentPlayerTurn = dealer;
    while(true) {
      PlayerState current = players[currentPlayerTurn];
      // Deal
      MahjongTile newTile = deck.remove();
      synchronized (current) {
        current.hand.getHidden().add(newTile);
        broadcastExcept(new GainTilePacket(MahjongTile.NULL, currentPlayerTurn), currentPlayerTurn, new GainTilePacket(newTile, currentPlayerTurn));
      }

      // Recieve main player action
      Packet p = current.controller.receive(10000);
      synchronized (current) {
        if (p.getId() == DiscardTilePacket.ID) {
          DiscardTilePacket pDerived = (DiscardTilePacket)p;
          int index = current.hand.getHidden().indexOf(((DiscardTilePacket)p).getTile());
          if(index == -1 || pDerived.getPlayer() != currentPlayerTurn) {
            unalivePlayer(current);
            continue;
          }
          current.hand.getHidden().remove(index);
          broadcast(p);
        } else {
          unalivePlayer(current);
          continue;
        }
      }

      // Receive other player's reactions
      // Dealer gets priority on move, didn't want the order to be unspecified
      for(int i = 0, pl = dealer; i < 4; i++, pl = (pl + 1) % 4) {
        PlayerState considerPlayer = players[pl];
        Packet p2 = considerPlayer.controller.receive(10000);
        if(p2.getId() == NoActionPacket.ID) {
          continue;
        } //TODO impl ronnya
      }

      // Next player turn
      currentPlayerTurn++;
      currentPlayerTurn %= 4;
    }
  }

  /**
   * Get the state associated with a specified player.
   * @param id The player to query
   * @return the player's state.
   */
  public PlayerState getPlayer(int id) {
    return players[id];
  }

}
