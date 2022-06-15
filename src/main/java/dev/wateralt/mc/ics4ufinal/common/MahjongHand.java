package dev.wateralt.mc.ics4ufinal.common;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Contains all the information of a Mahjong Hand.
 */
public class MahjongHand {
  // Yes. I know LinkedList works better here.
  // I dislike LinkedList for personal reasons.
  // Traversing a LinkedList might be slower because it plays worse with CPU caching (because non-locality)

  private final ArrayList<MahjongTile> hidden;
  private final ArrayList<MahjongTile> hiddenKan;
  private final ArrayList<MahjongTile> shown;
  private final ArrayList<MahjongTile> shownKan;
  private MahjongTile finalTile;

  public MahjongHand() {
    hidden = new ArrayList<>();
    hiddenKan = new ArrayList<>();
    shown = new ArrayList<>();
    shownKan = new ArrayList<>();
    finalTile = MahjongTile.NULL;
  }

  public void clear() {
    hidden.clear();
    hiddenKan.clear();
    finalTile = MahjongTile.NULL;
  }

  /**
   * @return the hidden part of the hand.
   */
  public ArrayList<MahjongTile> getHidden() {
    return hidden;
  }

  /**
   * @return a list the hidden kans called by this player.
   */
  public ArrayList<MahjongTile> getHiddenKan() {
    return hiddenKan;
  }

  /**
   *
   * @return a list of all chi, pon, or open kan called by this player.
   */
  public ArrayList<MahjongTile> getShown() {
    return shown;
  }

  public ArrayList<MahjongTile> getShownKan() {
    return shownKan;
  }

  /**
   *
   * @return the final tile (agari) that the player has won with.
   */
  public MahjongTile getFinalTile() {
    return finalTile;
  }

  /**
   *
   * @param finalTile sets the final tile (agari)
   */
  public void setFinalTile(MahjongTile finalTile) {
    this.finalTile = finalTile;
  }

  /**
   *
   * @return the total size of the hand, with all shown and hidden tiles and the last tile.
   */
  public int getLength() {
    return hidden.size() + hiddenKan.size() + shown.size() + (finalTile.equals(MahjongTile.NULL) ? 0 : 1);
  }

  /**
   * Checks equality with another mahjong hand.
   * @param o The other mahjong hand.
   * @return true if the two hands are equal.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MahjongHand that = (MahjongHand) o;
    return Objects.equals(hidden, that.hidden) && Objects.equals(hiddenKan, that.hiddenKan) && Objects.equals(shown, that.shown) && Objects.equals(shownKan, that.shownKan) && Objects.equals(finalTile, that.finalTile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hidden, hiddenKan, shown, finalTile);
  }
}
