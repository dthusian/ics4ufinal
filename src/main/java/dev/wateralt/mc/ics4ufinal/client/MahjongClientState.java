package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.util.ArrayList;

/**
 * <h2>Client State</h2>
 *
 * Tracks everything the client knows. Because the server won't send all the state every turn,
 * clients must track the current state of the game. Additionally, this object can also be read
 * by MahjongRenderer to render tiles and whatnot
 */
public class MahjongClientState {
  public static class PlayerAction {
    public static int NOTHING = 0;
    public static int DISCARD_TILE = 1;
    public static int CALL_TILE = 2;
    public static int SELECT_CHI = 3;
  }

  private final MahjongHand[] playerHands;
  private ArrayList<ArrayList<MahjongTile>> playerDiscardPiles;
  private int myPlayerId;
  private int playerAction;
  private ClientCallOptions callOptions;

  /**
   * Constructs a Client State with empty hands and discard piles
   */
  public MahjongClientState() {
    playerHands = new MahjongHand[] {
        new MahjongHand(),
        new MahjongHand(),
        new MahjongHand(),
        new MahjongHand()
    };
    playerDiscardPiles = new ArrayList<>();
    playerDiscardPiles.add(new ArrayList<>());
    playerDiscardPiles.add(new ArrayList<>());
    playerDiscardPiles.add(new ArrayList<>());
    playerDiscardPiles.add(new ArrayList<>());
    playerAction = PlayerAction.NOTHING;
    callOptions = null;
  }

  /**
   * Returns an array of each player's hand
   * @return an array of each player's hand
   */
  public MahjongHand[] getPlayerHands() {
    return playerHands;
  }

  /**
   * Returns the index of the current player, that is, the player represented by the Client object
   * @return The integer index of the current player
   */
  public int getMyPlayerId() {
    return myPlayerId;
  }

  /**
   * Sets the index of the current player. No class other than the Client class should call this method,
   * it will not allow you to assume control of another player.
   * @param myPlayerId The new index of the current player.
   */
  public void setMyPlayerId(int myPlayerId) {
    this.myPlayerId = myPlayerId;
  }

  public int getPlayerAction() {
    return playerAction;
  }

  public void setPlayerAction(int playerAction) {
    this.playerAction = playerAction;
  }

  public ArrayList<ArrayList<MahjongTile>> getPlayerDiscardPiles() {
    return playerDiscardPiles;
  }

  public MahjongHand getMyHand() {
    return playerHands[myPlayerId];
  }

  public ClientCallOptions getCallOptions() {
    return callOptions;
  }

  public void setCallOptions(ClientCallOptions callOptions) {
    this.callOptions = callOptions;
  }
}
