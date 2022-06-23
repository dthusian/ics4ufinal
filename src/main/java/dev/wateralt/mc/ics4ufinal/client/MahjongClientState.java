package dev.wateralt.mc.ics4ufinal.client;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.yaku.Yaku;

import java.util.ArrayList;
import java.util.Comparator;

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
    public static int ENDED_GAME = 4;
  }

  private final MahjongHand[] playerHands;
  private ArrayList<ArrayList<MahjongTile>> playerDiscardPiles;
  private int myPlayerId;
  private int playerAction;
  private ClientCallOptions callOptions;
  private boolean canTsumo;

  public int nPlayers;

  private MahjongHand winningHand;
  private ArrayList<Yaku.YakuEntry> winningYakus;
  private int winningId;

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
    canTsumo = false;
    nPlayers = 0;
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

  public boolean getCanTsumo() { return canTsumo; }

  public void setCanTsumo(boolean canTsumo) {
    this.canTsumo = canTsumo;
  }

  public void reportWin(MahjongHand winningHand, int winningId) {
    this.winningHand = winningHand;
    this.winningHand.getHidden().sort(Comparator.naturalOrder());
    this.winningYakus = Yaku.matchHand(winningHand);
    this.winningId = winningId;
  }

  public ArrayList<Yaku.YakuEntry> getWinningYakus() {
    return winningYakus;
  }

  public MahjongHand getWinningHand() {
    return winningHand;
  }

  public int getWinningId() {
    return winningId;
  }

  public void checkTsumo() {
    ArrayList<Yaku.YakuEntry> possibleWin = Yaku.matchHand(getMyHand());
    if(possibleWin != null && possibleWin.size() > 0) {
      canTsumo = true;
    }
  }

  public int getnPlayers() {
    return nPlayers;
  }

  public void setnPlayers(int nPlayers) {
    this.nPlayers = nPlayers;
  }
}
