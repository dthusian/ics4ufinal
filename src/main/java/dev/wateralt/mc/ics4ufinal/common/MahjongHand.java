package dev.wateralt.mc.ics4ufinal.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
  private MahjongTile finalTile;

  public MahjongHand() {
    hidden = new ArrayList<>();
    hiddenKan = new ArrayList<>();
    shown = new ArrayList<>();
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

  public boolean canPon(MahjongTile t) {
    return hidden.stream().filter(v -> v.equals(t)).count() >= 2;
  }

  public void doPon(MahjongTile t) {
    for(int i = 0; i < 2; i++) hidden.remove(t);
    for(int i = 0; i < 3; i++) shown.add(t);
  }

  public boolean canKan(MahjongTile t) {
    return hidden.stream().filter(v -> v.equals(t)).count() >= 3;
  }

  // Do not remove the tile from hidden before you pass it to these functions

  public void doKanFromExternalTile(MahjongTile t) {
    for(int i = 0; i < 3; i++) hidden.remove(t);
    for(int i = 0; i < 4; i++) shown.add(t);
  }

  public void doHiddenKan(MahjongTile t) {
    for(int i = 0; i < 4; i++) hidden.remove(t);
    for(int i = 0; i < 4; i++) hiddenKan.add(t);
  }

  public void doKanToUpdatePon(MahjongTile t) {
    hidden.remove(t);
  }

  public boolean canChi(MahjongTile t) {
    if(t.getSuit() == 'z') return false;
    int n = t.getNumber();
    // These initial values will never be read but Java is too dumb to know that
    boolean m2 = false, m1 = false, p1 = false, p2 = false;
    if(n >= 3) {
      m2 = hidden.contains(new MahjongTile(n - 2, t.getSuit()));
    }
    if(n >= 2) {
      m1 = hidden.contains(new MahjongTile(n - 1, t.getSuit()));
    }
    if(n <= 8) {
      p1 = hidden.contains(new MahjongTile(n + 1, t.getSuit()));
    }
    if(n <= 7) {
      p2 = hidden.contains(new MahjongTile(n + 2, t.getSuit()));
    }
    if(n == 1) {
      return p1 && p2;
    }
    if(n == 2) {
      return (m1 && p1) || (p1 && p2);
    }
    if(n == 8) {
      return (m1 && p1) || (m1 && m2);
    }
    if(n == 9) {
      return m1 && m2;
    }
    return (m1 && m2) || (m1 && p1) || (p1 && p2);
  }

  public boolean canChi(MahjongTile[] t) {
    if(t.length != 3) return false;
    return Arrays.stream(t).filter(v -> hidden.contains(v)).count() >= 2;
  }

  public void doChi(MahjongTile[] t) {
    Arrays.sort(t);
    hidden.removeAll(List.of(t));
    shown.addAll(List.of(t));
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
    return Objects.equals(hidden, that.hidden) && Objects.equals(hiddenKan, that.hiddenKan) && Objects.equals(shown, that.shown) && Objects.equals(finalTile, that.finalTile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hidden, hiddenKan, shown, finalTile);
  }
}
